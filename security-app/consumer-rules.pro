-keep class android.app.servertransaction.** { *; }

-keep class com.yyxnb.android.*{
 public <methods>;
}

-keep class com.yyxnb.android.intent.*{
 public <methods>;
}

-keep class com.yyxnb.android.secure.app.activity.protect.ActivityProtect{
 public <methods>;
 }

-keep class com.yyxnb.android.secure.app.activity.protect.ExceptionHandler{
 public <methods>;
 protected <methods>;
}

-keep interface com.yyxnb.android.secure.app.activity.protect.IActivityProtect{
 public <methods>;
}