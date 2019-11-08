package ru.acurresearch.dombyta_new.services;

import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import ru.acurresearch.dombyta.Activities.MainActivity;
import ru.evotor.framework.core.IntegrationService;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEvent;
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventProcessor;
import ru.evotor.framework.core.action.processor.ActionProcessor;

public class WorkService extends IntegrationService {
    public static final String EXTRA_NAME_OPERATION = "EXTRA_NAME_OPERATION";
    private static final String TAG = "WS";

    public WorkService() {
    }


    @Nullable
    @Override
    protected Map<String, ActionProcessor> createProcessors() {
        Map<String, ActionProcessor> map = new HashMap<>();
        map.put(ReceiptDiscountEvent.NAME_SELL_RECEIPT, new ReceiptDiscountEventProcessor() {
            @Override
            public void call(@NonNull String action, @NonNull ReceiptDiscountEvent event, @NonNull Callback callback) {
                Log.d(TAG, action);
                Log.e("FROM IntegrationService", "action");
                Intent intent = new Intent(WorkService.this, MainActivity.class);
                intent.putExtra(EXTRA_NAME_OPERATION, "sell");

                try {
                    callback.startActivity(intent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        return map;
    }
}
