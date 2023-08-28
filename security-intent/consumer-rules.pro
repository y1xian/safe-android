-keep class android.app.servertransaction.** { *; }

-keep class com.yyxnb.android.*{
 public <methods>;
}

-keep class com.yyxnb.android.intent.*{
 public <methods>;
}

-keep class com.yyxnb.android.activity.protect.ActivityProtect{
 public <methods>;
 }

-keep class com.yyxnb.android.activity.protect.ExceptionHandler{
 public <methods>;
 protected <methods>;
}

-keep interface com.yyxnb.android.activity.protect.IActivityProtect{
 public <methods>;
}