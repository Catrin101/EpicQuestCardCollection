package com.example.epicquestcardcollection.data.remote;

import android.os.AsyncTask;
import android.util.Log;

import com.example.epicquestcardcollection.model.HeroCard;
import com.example.epicquestcardcollection.model.PowerStats;
import com.example.epicquestcardcollection.utils.AppConstants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Cliente para consumir la SuperHero API y obtener datos de héroes.
 * Utiliza AsyncTask para realizar las peticiones HTTP en segundo plano.
 */
public class SuperHeroAPI {

    public interface HeroCallback {
        void onHeroReceived(HeroCard heroCard);
        void onError(String errorMessage);
    }

    /**
     * Obtiene un héroe aleatorio de la API
     */
    public static void getRandomHero(HeroCallback callback) {
        // La API tiene IDs desde 1 hasta ~730
        int randomId = new Random().nextInt(730) + 1;
        new FetchHeroTask(callback).execute(randomId);
    }

    /**
     * Obtiene un héroe específico por ID
     */
    public static void getHeroById(int heroId, HeroCallback callback) {
        new FetchHeroTask(callback).execute(heroId);
    }

    private static class FetchHeroTask extends AsyncTask<Integer, Void, HeroCard> {
        private final HeroCallback callback;
        private String errorMessage;

        public FetchHeroTask(HeroCallback callback) {
            this.callback = callback;
        }

        @Override
        protected HeroCard doInBackground(Integer... params) {
            int heroId = params[0];
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Construir URL de la API
                String apiUrl = AppConstants.SUPERHERO_API_BASE_URL +
                        AppConstants.API_ACCESS_TOKEN + "/" + heroId;

                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.connect();

                // Leer respuesta
                InputStream inputStream = connection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    errorMessage = "Respuesta vacía de la API";
                    return null;
                }

                String jsonResponse = buffer.toString();
                return parseHeroFromJson(jsonResponse, heroId);

            } catch (Exception e) {
                Log.e("SuperHeroAPI", "Error fetching hero: " + e.getMessage());
                errorMessage = "Error de conexión: " + e.getMessage();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e) {
                    Log.e("SuperHeroAPI", "Error closing stream", e);
                }
            }
        }

        @Override
        protected void onPostExecute(HeroCard heroCard) {
            if (heroCard != null) {
                callback.onHeroReceived(heroCard);
            } else {
                callback.onError(errorMessage != null ? errorMessage : "Error desconocido");
            }
        }
    }

    /**
     * Parsea la respuesta JSON de la API a un objeto HeroCard
     */
    private static HeroCard parseHeroFromJson(String jsonResponse, int heroId) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            if (!jsonObject.getString("response").equals("success")) {
                return null;
            }

            // Información básica
            String name = jsonObject.getString("name");
            String id = String.valueOf(heroId);

            // Biografía
            JSONObject biography = jsonObject.getJSONObject("biography");
            String fullName = biography.getString("full-name");
            String publisher = biography.getString("publisher");
            String biographyText = fullName + " - " + publisher;

            // Imagen
            JSONObject image = jsonObject.getJSONObject("image");
            String imageUrl = image.getString("url");

            // Estadísticas de poder
            JSONObject powerstats = jsonObject.getJSONObject("powerstats");
            PowerStats stats = new PowerStats();
            stats.setIntelligence(safeParseInt(powerstats.getString("intelligence")));
            stats.setStrength(safeParseInt(powerstats.getString("strength")));
            stats.setSpeed(safeParseInt(powerstats.getString("speed")));
            stats.setDurability(safeParseInt(powerstats.getString("durability")));
            stats.setPower(safeParseInt(powerstats.getString("power")));
            stats.setCombat(safeParseInt(powerstats.getString("combat")));

            // Crear y retornar HeroCard
            return new HeroCard(id, name, biographyText, imageUrl, stats);

        } catch (Exception e) {
            Log.e("SuperHeroAPI", "Error parsing JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convierte string a int de forma segura
     */
    private static int safeParseInt(String value) {
        try {
            // Algunos valores vienen como "null" en la API
            if (value.equals("null")) {
                return 0;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
