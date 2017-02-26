package com.zhanggb.fictionread.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.KeyEvent;

/**
 * Created with IntelliJ IDEA.
 * User: zhanggb
 * Date: 11-9-23
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */
public class DialogFactory {

    public static AlertDialog createChooseDialog(
            Context context,
            String title,
            String[] items,
            DialogInterface.OnClickListener positiveOnClickListener
    ) {
        return new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(title)
                .setItems(items, positiveOnClickListener)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                        }
                        return false;
                    }
                }).create();
    }


    public static ProgressDialog createProgressDialog(
            Context context, String message, ProgressDialogCallback callback) {
        ProgressDialog progressDialog = new WrapperProgressDialg(context, callback);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_SEARCH || i == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        return progressDialog;
    }

    public static interface ProgressDialogCallback {

        void onShow(ProgressDialog dialog);

        void onDismiss(ProgressDialog dialog);

        void onCancel(ProgressDialog dialog);

        void onException(ProgressDialog dialog, Exception exception);

        public static class Adapter implements ProgressDialogCallback {
            @Override
            public void onShow(ProgressDialog dialog) {
            }

            @Override
            public void onDismiss(ProgressDialog dialog) {
            }

            @Override
            public void onCancel(ProgressDialog dialog) {
            }

            public void onException(ProgressDialog dialog, Exception exception) {

            }
        }
    }


    private static class WrapperProgressDialg extends ProgressDialog {

        private ProgressDialogCallback callback;

        private Handler handler;

        private WrapperProgressDialg(Context context, ProgressDialogCallback callback) {
            super(context);
            this.callback = callback;
            handler = new Handler();
        }

        @Override
        public void show() {
            try {
                super.show();
            } catch (Throwable t) {
            }
            startTaskThread();
        }


        private void startTaskThread() {
            final ProgressDialog instance = this;
            new Thread() {
                @Override
                public void run() {
                    try {
                        if (callback != null) {
                            callback.onShow(instance);
                        }
                        Thread.sleep(1000);
                    } catch (final Exception ex) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    ex.printStackTrace();
                                    callback.onException(instance, ex);
                                }
                            }
                        });
                    } finally {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (callback != null) {
                                        callback.onDismiss(instance);
                                    }
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        });
                        try {
                            instance.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }

    }
}
