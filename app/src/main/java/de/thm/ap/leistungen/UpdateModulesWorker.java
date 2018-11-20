package de.thm.ap.leistungen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import de.thm.ap.leistungen.data.ModuleDAO;
import de.thm.ap.leistungen.model.Module;

public class UpdateModulesWorker extends Worker {
    private static final String MODULES_URL = "https://homepages.thm.de/~hg10187/modules.json";
    private Context context = null;
    private static final int NOTIFICATION_ID = 42;
    private static final String CHANNEL_ID = "4711";

    public UpdateModulesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    public Result doWork() {
        NotificationManager notifyManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID, "Channel name", NotificationManager.IMPORTANCE_LOW);
            notifyManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        notifyBuilder
                .setContentTitle(getApplicationContext().getString(R.string.refresh_modules))
                .setSmallIcon(R.drawable.ic_autorenew_black_24dp)
                .setContentText(getApplicationContext().getString(R.string.check_server_data))
                .setProgress(0, 0, true);
        notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        SharedPreferences sharedPrefs = getApplicationContext()
                .getSharedPreferences("modules", Context.MODE_PRIVATE);
        HttpURLConnection connection = null;
        InputStream in = null;
        ModuleDAO moduleDAO = new ModuleDAO(context);
        try {
            connection = (HttpURLConnection) new URL(MODULES_URL).openConnection();
            connection.setIfModifiedSince(sharedPrefs.getLong("lastModified", 0));
            if (connection.getResponseCode() == 200) {
                notifyBuilder.setContentText(getApplicationContext().getString(R.string.loading_new_data));
                notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
                in = connection.getInputStream();
                Module[] modules = new Gson().fromJson(new InputStreamReader(in), Module[].class);
                moduleDAO.deleteAll();
                moduleDAO.persistAll(modules);
                sharedPrefs.edit()
                        .putLong("lastModified", connection.getLastModified())
                        .apply();

                notifyBuilder.setContentText(getApplicationContext().getString(R.string.loaded_new_data_success))
                        .setProgress(0, 0, false);
                notifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

            } else {
                notifyManager.cancel(NOTIFICATION_ID);
            }
        } catch (IOException e) {
            notifyManager.cancel(NOTIFICATION_ID);
            return Result.FAILURE;
        } finally {
            IOUtils.closeQuietly(in);
            if (connection != null) {
                connection.disconnect();
            }
        }
        return Result.SUCCESS;
    }
}
