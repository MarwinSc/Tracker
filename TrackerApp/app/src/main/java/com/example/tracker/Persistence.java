package com.example.tracker;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Persistence {

    private Context context;
    private ArrayList<String> labels;
    private ArrayList<Integer> values;

    public Persistence(Context context) {
        this.context = context;
        this.labels = new ArrayList<String>();
        this.values = new ArrayList<Integer>();
        init();
    }

    private void init() {

        File tmpDir = new File(context.getFilesDir() + "/data.json");
        boolean exists = tmpDir.exists();

        // if no data.json file exists
        // else retrieve the labels for the buttons, as sideeffect add the new day to data (read())
        if (!exists) {
            JSONArray file = new JSONArray();
            file.put(setUpDay());
            writeToFile(file.toString(), this.context);
        }else{
            read();
        }
    }

    /**
     * Reads all data from the disk.
     * As a sideeffect compareDataAndCache is called.
     * @return
     */
    private JSONArray read(){

        JSONArray file = new JSONArray();

        try {
            StringBuilder plaintext = new StringBuilder();

            FileReader fr = new FileReader(context.getFilesDir() + "/data.json");
            int content;
            while((content = fr.read())!=-1){
                plaintext.append((char) content);
            }

            file = new JSONArray(plaintext.toString());
            read_sideeffects(file);
            file = compareDataAndCache(file);
            return file;

        } catch (IOException | JSONException e) {
            Log.e("Exception","error in read(): "+ e.getMessage());
        }
        return null;
    }
    private void read_sideeffects(JSONArray file){
        this.labels = getLatestLabels(file,1);
        this.values = getLatestValues(file,1);
    }

    /**
     * Compares the date of the written file on disk and the current cached data.
     * If today is different from the newest date of the written data, a new day is added to the data and everything is written to disk.
     * @param fetchedData
     */
    private JSONArray compareDataAndCache(JSONArray fetchedData){
        try {
            //retrieve the last written day and its data
            JSONObject lastDay = fetchedData.getJSONObject(fetchedData.length()-1);
            Date dataDate = new SimpleDateFormat("dd-MM-yyyy").parse((String) lastDay.get("date"));

            Date today = new SimpleDateFormat("dd-MM-yyyy").parse((String) setUpDay().get("date"));

            //check if today is already in data,
            //if not put and write
            if (!today.equals(dataDate) && labels.size() > 0){
                fetchedData = addNewDay(fetchedData);
            }
        } catch (JSONException | ParseException e) {
            Log.e("Exception","error in Date compare: " + e.getMessage());
        }

        return fetchedData;
    }

    /**
     * Adds a day filled with the current labels to the given data.
     * @param fetchedData
     */
    private JSONArray addNewDay(JSONArray fetchedData){
        fetchedData.put(setUpDay());
        writeToFile(fetchedData.toString(), this.context);
        for (String s : labels) {
            //TODO unnecessary IO here by calling read() in for loop... remove
            fetchedData = addHabit(s, 0);
        }
        return fetchedData;
    }

    /**
     * Retrieves the labels of the latest day in the given data.
     * @param fetchedData
     * @return
     */
    private ArrayList<String> getLatestLabels(JSONArray fetchedData, int day_offset){
        ArrayList<String> labels = new ArrayList<String>();
        try {
            JSONObject dataLastDay = fetchedData.getJSONObject(fetchedData.length()-day_offset).getJSONObject("data");
            for(int i = 0; i < dataLastDay.names().length(); i++){
                String habitName = dataLastDay.names().getString(i);
                labels.add(habitName);
            }
        } catch (JSONException e) {
            Log.e("Exception","error in setCurrentLabels: " + e.getMessage());
        } catch (NullPointerException e){
            Log.w("Warning", "no previous labels");
        }
        return labels;
    }

    /**
     * Retrieves the values of the latest day in the given data.
     */
    private ArrayList<Integer> getLatestValues(JSONArray fetchedData, int day_offset){
        ArrayList<Integer> values = new ArrayList<Integer>();
        try {
            JSONObject dataLastDay = fetchedData.getJSONObject(fetchedData.length()-day_offset).getJSONObject("data");
            for(int i = 0; i < dataLastDay.names().length(); i++){
                int habitValue = dataLastDay.getInt(dataLastDay.names().getString(i));
                values.add(habitValue);
            }
        } catch (JSONException e) {
            Log.e("Exception","error in setCurrentValues: " + e.getMessage());
        } catch (NullPointerException e){
            Log.w("Warning", "no previous labels");
        }
        return values;
    }

    /**
     * Returns a JSONObject for the current day with an empty data object.
     * @return
     */
    private JSONObject setUpDay(){
        JSONObject day = new JSONObject();
        Date today = new Date();
        try {
            day.put("date", new SimpleDateFormat("dd-MM-yyyy").format(today));
            JSONObject data = new JSONObject();
            day.put("data", data);
            JSONObject time = new JSONObject();
            day.put("time", time);
        } catch (JSONException e) {
            Log.e("Exception","error in setUpDay: " + e.getMessage());
        }
        return day;
    }

    /**
     * Writes the cached data to disk.
     * @param data
     * @param context
     */
    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("data.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    /**
     * Writes a data.json file containing an old date.
     */
    public void testByWritingOldDate(){

        Util.deleteRecursive(context.getFilesDir());

        JSONArray testData = new JSONArray();
        JSONObject day = new JSONObject();
        Date yesterday = new Date(System.currentTimeMillis()-24*60*60*1000);
        try {
            day.put("date", new SimpleDateFormat("dd-MM-yyyy").format(yesterday));
            JSONObject data = new JSONObject();
            data.put("Meal",42);
            day.put("data", data);
            JSONArray times = new JSONArray();
            times.put("12:00:00");
            JSONObject time = new JSONObject();
            time.put("Meal",times);
            day.put("time", time);
            testData.put(day);
        } catch (JSONException e) {
            Log.e("Exception","error in test: " + e.getMessage());
        }
        writeToFile(testData.toString(),context);
    }

    /**
     * Reads the file from disk increments the
     * @param feature
     * @param day_offset the day to use, 1 is today, 2 yesterday ...
     */
    public int incrementFeature(String feature, int day_offset){
        JSONArray file = read();
        int value;
        Date today = new Date();
        try {
            JSONObject day = file.getJSONObject(file.length()-day_offset);
            JSONObject data = (JSONObject) day.get("data");
            value = (int) data.get(feature);
            data.put(feature,value+1);
            //add click time
            if (day_offset < 2) {
                JSONObject time = (JSONObject) day.get("time");
                JSONArray times = (JSONArray) time.get(feature);
                times.put(new SimpleDateFormat("HH:mm:ss").format(today));
            }else{
                //if incrementing for previous day
                JSONObject time = (JSONObject) day.get("time");
                JSONArray times = (JSONArray) time.get(feature);
                times.put("23:59:00");
            }
            writeToFile(file.toString(), this.context);
            return value+1;
        } catch (JSONException e) {
            Log.e("Exception","error in increment(): " + e.getMessage());
            return -1;
        }
    }

    /**
     * Add a Habit to the written file;
     * @param habitName
     */
    public JSONArray addHabit(String habitName, int value){

        JSONArray file = read();

        if (!labels.contains(habitName)){labels.add(habitName);}

        try {
            JSONObject day = file.getJSONObject(file.length()-1);
            JSONObject data = (JSONObject) day.get("data");
            data.put(habitName,value);
            //add click time
            JSONObject time = (JSONObject) day.get("time");
            JSONArray times = new JSONArray();
            time.put(habitName,times);

            writeToFile(file.toString(), this.context);
            return file;

        } catch (JSONException e) {
            Log.e("Exception","error in adding Habit: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<String> getLabels(int day_offset){
        JSONArray file = read();
        if (day_offset > 1){
            return getLatestLabels(file,day_offset);
        }
        return this.labels;
    }

    public ArrayList<Integer> getValues(int day_offset){
        JSONArray file = read();
        if (day_offset > 1){
            return getLatestValues(file,day_offset);
        }
        return this.values;
    }

    /**
     * Check if the file for today alreay contains comments and either creates the array and adds the new entry or just adds it.
     * @param text
     */
    public void addComment(String text) {
        JSONArray file = read();
        try {
            JSONObject day = file.getJSONObject(file.length()-1);
            if (day.has("comments")){
                JSONArray comments = (JSONArray) day.get("comments");
                comments.put(text);
            }else{
                JSONArray comments = new JSONArray();
                comments.put(text);
                day.put("comments",comments);
            }

            writeToFile(file.toString(), this.context);
        } catch (JSONException e) {
            Log.e("Exception","error in adding comment: " + e.getMessage());
        }
    }

    /**
     * sets the persisted mood to either bad:1 neutral:2 good:3
     * @param mood
     */
    public void setMood(int mood) {
        JSONArray file = read();
        try {
            JSONObject day = file.getJSONObject(file.length()-1);
            JSONObject data = (JSONObject) day.get("data");

            data.put("Mood",mood);

            writeToFile(file.toString(), this.context);
        } catch (JSONException e) {
            Log.e("Exception","error in set mood: " + e.getMessage());
        }
    }

    public void addPhoneUnlockTime(){
        JSONArray file = read();
        try {
            Date today = new Date();
            JSONObject day = file.getJSONObject(file.length()-1);
            JSONObject time = (JSONObject) day.get("time");
            if (!time.has("Unlock")) {
                JSONArray times = new JSONArray();
                times.put(new SimpleDateFormat("HH:mm:ss").format(today));
                time.put("Unlock",times);
            }else{
                JSONArray times = (JSONArray) time.get("Unlock");
                times.put(new SimpleDateFormat("HH:mm:ss").format(today));
            }
            writeToFile(file.toString(), this.context);
        } catch (JSONException e) {
            Log.e("Exception","error in writing unlock time: " + e.getMessage());
        }
    }
}
