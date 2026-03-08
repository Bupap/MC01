import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class JsonLoader {

    public static Map<String, Plant> loadPlants(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject json = new JSONObject(content);
        Map<String, Plant> plants = new HashMap<>();

        for (String key : json.keySet()) {
            JSONObject obj = json.getJSONObject(key);
            plants.put(key, new Plant(
                    obj.getString("name"),
                    obj.getInt("price"),
                    obj.getInt("yield"),
                    obj.getInt("max_growth"),
                    obj.getString("preferred_soil"),
                    obj.getInt("crop_price")
            ));
        }
        return plants;
    }

    public static Map<String, String> loadMapLegend(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject json = new JSONObject(content);
        JSONObject legendJson = json.getJSONObject("legend");

        Map<String, String> legend = new HashMap<>();
        for (String key : legendJson.keySet()) {
            legend.put(legendJson.getString(key), key);
        }
        return legend;
    }

    public static String[][] loadMapGrid(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject json = new JSONObject(content);
        JSONArray mapArray = json.getJSONArray("map");

        String[][] grid = new String[10][10];
        for (int i = 0; i < mapArray.length(); i++) {
            JSONArray row = mapArray.getJSONArray(i);
            for (int j = 0; j < row.length(); j++) {
                grid[i][j] = row.getString(j);
            }
        }
        return grid;
    }

    public static Map<String, HighScoreEntry> loadHighScores(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject json = new JSONObject(content);
        Map<String, HighScoreEntry> scores = new TreeMap<>();

        for (String rank : json.keySet()) {
            JSONObject obj = json.getJSONObject(rank);
            scores.put(rank, new HighScoreEntry(
                    obj.getString("name"),
                    obj.getInt("savings")
            ));
        }
        return scores;
    }

    public static Map<String, Fertilizer> loadFertilizers(String filePath) throws Exception {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JSONObject json = new JSONObject(content);
        Map<String, Fertilizer> fertilizers = new HashMap<>();

        for (String key : json.keySet()) {
            JSONObject obj = json.getJSONObject(key);
            fertilizers.put(key, new Fertilizer(
                    obj.getString("name"),
                    obj.getInt("price"),
                    obj.getInt("days")
            ));
        }
        return fertilizers;
    }
}