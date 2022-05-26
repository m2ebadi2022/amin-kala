package ir.taravatgroup.aminkala;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "ir.taravatgroup.aminkala", "ir.taravatgroup.aminkala.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "ir.taravatgroup.aminkala", "ir.taravatgroup.aminkala.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "ir.taravatgroup.aminkala.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _tim = null;
public anywheresoftware.b4a.objects.WebViewWrapper _webview1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _pan_menu = null;
public anywheresoftware.b4a.objects.PanelWrapper _pan_all_menu = null;
public anywheresoftware.b4a.phone.Phone _phon = null;
public anywheresoftware.b4a.objects.PanelWrapper _pan_notconn = null;
public static int _current_page = 0;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_shop_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_class_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_liked_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_info_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_suport_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_marjoe_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_qus_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_account_m = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_setting = null;
public anywheresoftware.b4a.objects.ScrollViewWrapper _scview_menu = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_account = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_cart = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_class = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_shop = null;
public anywheresoftware.b4a.objects.LabelWrapper _account_icon = null;
public anywheresoftware.b4a.objects.LabelWrapper _cart_icon = null;
public anywheresoftware.b4a.objects.LabelWrapper _class_icon = null;
public anywheresoftware.b4a.objects.LabelWrapper _shop_icon = null;
public static String _str_shop = "";
public static String _str_shop2 = "";
public static String _str_class = "";
public static String _str_cart = "";
public static String _str_accont = "";
public static String _str_fav = "";
public static String _str_about = "";
public static String _str_call = "";
public static String _str_marjoee = "";
public static String _str_ques = "";
public anywheresoftware.b4a.objects.LabelWrapper _lbl_title = null;
public static int _type_app = 0;
public anywheresoftware.b4a.samples.httputils2.httpjob _ht = null;
public anywheresoftware.b4a.phone.Phone _phon1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbl_share_app = null;
public ir.taravatgroup.aminkala.b4xloadingindicator _loading = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public ir.taravatgroup.aminkala.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _account_icon_click() throws Exception{
 //BA.debugLineNum = 199;BA.debugLine="Private Sub account_icon_Click";
 //BA.debugLineNum = 200;BA.debugLine="lbl_account_Click";
_lbl_account_click();
 //BA.debugLineNum = 201;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 78;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 79;BA.debugLine="Activity.LoadLayout(\"Layout\")";
mostCurrent._activity.LoadLayout("Layout",mostCurrent.activityBA);
 //BA.debugLineNum = 80;BA.debugLine="tim.Initialize(\"tim\",4000)";
_tim.Initialize(processBA,"tim",(long) (4000));
 //BA.debugLineNum = 81;BA.debugLine="tim.Enabled=True";
_tim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 82;BA.debugLine="WebView1.JavaScriptEnabled=True";
mostCurrent._webview1.setJavaScriptEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 87;BA.debugLine="scview_menu.Panel.LoadLayout(\"menu_item\")";
mostCurrent._scview_menu.getPanel().LoadLayout("menu_item",mostCurrent.activityBA);
 //BA.debugLineNum = 88;BA.debugLine="des_menu";
_des_menu();
 //BA.debugLineNum = 90;BA.debugLine="If (File.Exists(File.DirInternal,\"setstart1\")==Tr";
if ((anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"setstart1")==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 91;BA.debugLine="lbl_setting.Visible=False";
mostCurrent._lbl_setting.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 92;BA.debugLine="type_app=1";
_type_app = (int) (1);
 //BA.debugLineNum = 93;BA.debugLine="http_initial_1(1)";
_http_initial_1((int) (1));
 }else if((anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"setstart2")==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 95;BA.debugLine="http_initial_1(2)";
_http_initial_1((int) (2));
 //BA.debugLineNum = 96;BA.debugLine="type_app=2";
_type_app = (int) (2);
 //BA.debugLineNum = 97;BA.debugLine="OpenBrowser(str_shop)";
_openbrowser(mostCurrent._str_shop);
 //BA.debugLineNum = 99;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 }else {
 //BA.debugLineNum = 101;BA.debugLine="type_app=0";
_type_app = (int) (0);
 //BA.debugLineNum = 102;BA.debugLine="http_initial_1(0)";
_http_initial_1((int) (0));
 };
 //BA.debugLineNum = 106;BA.debugLine="lbl_shop_Click";
_lbl_shop_click();
 //BA.debugLineNum = 107;BA.debugLine="lbl_title.Text=\"امین کالا\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("امین کالا"));
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _res1 = 0;
 //BA.debugLineNum = 467;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 468;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 469;BA.debugLine="If(pan_all_menu.Visible==True)Then";
if ((mostCurrent._pan_all_menu.getVisible()==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 470;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 }else if((_current_page!=1)) { 
 //BA.debugLineNum = 472;BA.debugLine="shop_icon_Click";
_shop_icon_click();
 }else if(((mostCurrent._webview1.getUrl()).equals(mostCurrent._str_shop) == false)) { 
 //BA.debugLineNum = 474;BA.debugLine="shop_icon_Click";
_shop_icon_click();
 }else {
 //BA.debugLineNum = 477;BA.debugLine="Dim res1 As Int";
_res1 = 0;
 //BA.debugLineNum = 478;BA.debugLine="res1 = Msgbox2(\"آیا قصد خروج دارید؟\", \"خروج\", \"";
_res1 = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("آیا قصد خروج دارید؟"),BA.ObjectToCharSequence("خروج"),"بله","","خیر",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 479;BA.debugLine="If res1 = DialogResponse.Positive Then";
if (_res1==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 480;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 };
 //BA.debugLineNum = 485;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 487;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 489;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 172;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 174;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 168;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 170;BA.debugLine="End Sub";
return "";
}
public static String  _cart_icon_click() throws Exception{
 //BA.debugLineNum = 214;BA.debugLine="Private Sub cart_icon_Click";
 //BA.debugLineNum = 215;BA.debugLine="current_page=3";
_current_page = (int) (3);
 //BA.debugLineNum = 216;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 217;BA.debugLine="lbl_title.Text=\"سبد خرید\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("سبد خرید"));
 //BA.debugLineNum = 218;BA.debugLine="WebView1.LoadUrl(str_cart)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_cart);
 //BA.debugLineNum = 221;BA.debugLine="End Sub";
return "";
}
public static boolean  _check_conn() throws Exception{
String _color_act = "";
String _color_non = "";
boolean _connected = false;
 //BA.debugLineNum = 321;BA.debugLine="Sub check_conn As Boolean";
 //BA.debugLineNum = 322;BA.debugLine="Dim color_act As String =0xFFF44336        '0xFFF";
_color_act = BA.NumberToString(((int)0xfff44336));
 //BA.debugLineNum = 323;BA.debugLine="Dim color_non As String =0xFF000000             '";
_color_non = BA.NumberToString(((int)0xff000000));
 //BA.debugLineNum = 325;BA.debugLine="lbl_shop_m.Color=Colors.White";
mostCurrent._lbl_shop_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 326;BA.debugLine="lbl_class_m.Color=Colors.White";
mostCurrent._lbl_class_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 327;BA.debugLine="lbl_liked_m.Color=Colors.White";
mostCurrent._lbl_liked_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 328;BA.debugLine="lbl_info_m.Color=Colors.White";
mostCurrent._lbl_info_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 329;BA.debugLine="lbl_suport_m.Color=Colors.White";
mostCurrent._lbl_suport_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 330;BA.debugLine="lbl_marjoe_m.Color=Colors.White";
mostCurrent._lbl_marjoe_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 331;BA.debugLine="lbl_qus_m.Color=Colors.White";
mostCurrent._lbl_qus_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 332;BA.debugLine="lbl_account_m.Color=Colors.White";
mostCurrent._lbl_account_m.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 334;BA.debugLine="lbl_shop_m.TextColor=color_non   'yes";
mostCurrent._lbl_shop_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 335;BA.debugLine="lbl_class_m.TextColor=color_non  ' no";
mostCurrent._lbl_class_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 336;BA.debugLine="lbl_liked_m.TextColor=color_non";
mostCurrent._lbl_liked_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 337;BA.debugLine="lbl_info_m.TextColor=color_non";
mostCurrent._lbl_info_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 338;BA.debugLine="lbl_suport_m.TextColor=color_non";
mostCurrent._lbl_suport_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 339;BA.debugLine="lbl_marjoe_m.TextColor=color_non";
mostCurrent._lbl_marjoe_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 340;BA.debugLine="lbl_qus_m.TextColor=color_non";
mostCurrent._lbl_qus_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 341;BA.debugLine="lbl_account_m.TextColor=color_non";
mostCurrent._lbl_account_m.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 342;BA.debugLine="lbl_cart.TextColor=color_non";
mostCurrent._lbl_cart.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 343;BA.debugLine="lbl_class.TextColor=color_non";
mostCurrent._lbl_class.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 344;BA.debugLine="lbl_shop.TextColor=color_non";
mostCurrent._lbl_shop.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 345;BA.debugLine="account_icon.TextColor=color_non";
mostCurrent._account_icon.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 346;BA.debugLine="shop_icon.TextColor=color_non";
mostCurrent._shop_icon.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 347;BA.debugLine="class_icon.TextColor=color_non";
mostCurrent._class_icon.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 348;BA.debugLine="cart_icon.TextColor=color_non";
mostCurrent._cart_icon.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 349;BA.debugLine="lbl_account.TextColor=color_non";
mostCurrent._lbl_account.setTextColor((int)(Double.parseDouble(_color_non)));
 //BA.debugLineNum = 350;BA.debugLine="Select current_page";
switch (_current_page) {
case 1: {
 //BA.debugLineNum = 352;BA.debugLine="lbl_shop_m.TextColor=Colors.White";
mostCurrent._lbl_shop_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 353;BA.debugLine="lbl_shop.TextColor=color_act";
mostCurrent._lbl_shop.setTextColor((int)(Double.parseDouble(_color_act)));
 //BA.debugLineNum = 354;BA.debugLine="shop_icon.TextColor=color_act";
mostCurrent._shop_icon.setTextColor((int)(Double.parseDouble(_color_act)));
 //BA.debugLineNum = 355;BA.debugLine="lbl_shop_m.Color=color_act";
mostCurrent._lbl_shop_m.setColor((int)(Double.parseDouble(_color_act)));
 break; }
case 2: {
 //BA.debugLineNum = 357;BA.debugLine="lbl_class_m.TextColor=Colors.White";
mostCurrent._lbl_class_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 358;BA.debugLine="lbl_class_m.Color=color_act";
mostCurrent._lbl_class_m.setColor((int)(Double.parseDouble(_color_act)));
 //BA.debugLineNum = 359;BA.debugLine="lbl_class.TextColor=color_act";
mostCurrent._lbl_class.setTextColor((int)(Double.parseDouble(_color_act)));
 //BA.debugLineNum = 360;BA.debugLine="class_icon.TextColor=color_act";
mostCurrent._class_icon.setTextColor((int)(Double.parseDouble(_color_act)));
 break; }
case 3: {
 //BA.debugLineNum = 362;BA.debugLine="lbl_cart.TextColor=color_act";
mostCurrent._lbl_cart.setTextColor((int)(Double.parseDouble(_color_act)));
 //BA.debugLineNum = 363;BA.debugLine="cart_icon.TextColor=color_act";
mostCurrent._cart_icon.setTextColor((int)(Double.parseDouble(_color_act)));
 break; }
case 4: {
 //BA.debugLineNum = 365;BA.debugLine="lbl_account.TextColor=color_act";
mostCurrent._lbl_account.setTextColor((int)(Double.parseDouble(_color_act)));
 //BA.debugLineNum = 366;BA.debugLine="account_icon.TextColor=color_act";
mostCurrent._account_icon.setTextColor((int)(Double.parseDouble(_color_act)));
 //BA.debugLineNum = 367;BA.debugLine="lbl_account_m.TextColor=Colors.White";
mostCurrent._lbl_account_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 368;BA.debugLine="lbl_account_m.Color=color_act";
mostCurrent._lbl_account_m.setColor((int)(Double.parseDouble(_color_act)));
 break; }
case 5: {
 //BA.debugLineNum = 370;BA.debugLine="lbl_liked_m.TextColor=Colors.White";
mostCurrent._lbl_liked_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 371;BA.debugLine="lbl_liked_m.Color=color_act";
mostCurrent._lbl_liked_m.setColor((int)(Double.parseDouble(_color_act)));
 break; }
case 6: {
 //BA.debugLineNum = 373;BA.debugLine="lbl_info_m.TextColor=Colors.White";
mostCurrent._lbl_info_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 374;BA.debugLine="lbl_info_m.Color=color_act";
mostCurrent._lbl_info_m.setColor((int)(Double.parseDouble(_color_act)));
 break; }
case 7: {
 //BA.debugLineNum = 376;BA.debugLine="lbl_suport_m.TextColor=Colors.White";
mostCurrent._lbl_suport_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 377;BA.debugLine="lbl_suport_m.Color=color_act";
mostCurrent._lbl_suport_m.setColor((int)(Double.parseDouble(_color_act)));
 break; }
case 8: {
 //BA.debugLineNum = 379;BA.debugLine="lbl_marjoe_m.TextColor=Colors.White";
mostCurrent._lbl_marjoe_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 380;BA.debugLine="lbl_marjoe_m.Color=color_act";
mostCurrent._lbl_marjoe_m.setColor((int)(Double.parseDouble(_color_act)));
 break; }
case 9: {
 //BA.debugLineNum = 382;BA.debugLine="lbl_qus_m.TextColor=Colors.White";
mostCurrent._lbl_qus_m.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 383;BA.debugLine="lbl_qus_m.Color=color_act";
mostCurrent._lbl_qus_m.setColor((int)(Double.parseDouble(_color_act)));
 break; }
}
;
 //BA.debugLineNum = 391;BA.debugLine="Dim connected As Boolean =False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 392;BA.debugLine="If phon.GetDataState=\"CONNECTED\" Then";
if ((mostCurrent._phon.GetDataState()).equals("CONNECTED")) { 
 //BA.debugLineNum = 393;BA.debugLine="connected=True";
_connected = anywheresoftware.b4a.keywords.Common.True;
 }else if((mostCurrent._phon.GetSettings("wifi_on")).equals(BA.NumberToString(1))) { 
 //BA.debugLineNum = 395;BA.debugLine="connected=True";
_connected = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 397;BA.debugLine="If (connected=False)Then";
if ((_connected==anywheresoftware.b4a.keywords.Common.False)) { 
 //BA.debugLineNum = 398;BA.debugLine="pan_notconn.Visible=True";
mostCurrent._pan_notconn.setVisible(anywheresoftware.b4a.keywords.Common.True);
 }else {
 //BA.debugLineNum = 400;BA.debugLine="pan_notconn.Visible=False";
mostCurrent._pan_notconn.setVisible(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 405;BA.debugLine="loading.Show";
mostCurrent._loading._show /*String*/ ();
 //BA.debugLineNum = 407;BA.debugLine="Return connected";
if (true) return _connected;
 //BA.debugLineNum = 410;BA.debugLine="End Sub";
return false;
}
public static String  _class_icon_click() throws Exception{
 //BA.debugLineNum = 223;BA.debugLine="Private Sub class_icon_Click";
 //BA.debugLineNum = 224;BA.debugLine="lbl_class_Click";
_lbl_class_click();
 //BA.debugLineNum = 225;BA.debugLine="End Sub";
return "";
}
public static String  _des_menu() throws Exception{
 //BA.debugLineNum = 151;BA.debugLine="Sub des_menu";
 //BA.debugLineNum = 152;BA.debugLine="lbl_shop_m.Width=scview_menu.Width";
mostCurrent._lbl_shop_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 153;BA.debugLine="lbl_class_m.Width=scview_menu.Width";
mostCurrent._lbl_class_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 154;BA.debugLine="lbl_liked_m.Width=scview_menu.Width";
mostCurrent._lbl_liked_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 155;BA.debugLine="lbl_info_m.Width=scview_menu.Width";
mostCurrent._lbl_info_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 156;BA.debugLine="lbl_suport_m.Width=scview_menu.Width";
mostCurrent._lbl_suport_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 157;BA.debugLine="lbl_marjoe_m.Width=scview_menu.Width";
mostCurrent._lbl_marjoe_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 158;BA.debugLine="lbl_qus_m.Width=scview_menu.Width";
mostCurrent._lbl_qus_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 159;BA.debugLine="lbl_account_m.Width=scview_menu.Width";
mostCurrent._lbl_account_m.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 160;BA.debugLine="lbl_share_app.Width=scview_menu.Width";
mostCurrent._lbl_share_app.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 161;BA.debugLine="lbl_setting.Width=scview_menu.Width";
mostCurrent._lbl_setting.setWidth(mostCurrent._scview_menu.getWidth());
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 21;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Private WebView1 As WebView";
mostCurrent._webview1 = new anywheresoftware.b4a.objects.WebViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private pan_menu As Panel";
mostCurrent._pan_menu = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private pan_all_menu As Panel";
mostCurrent._pan_all_menu = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim phon As Phone";
mostCurrent._phon = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 29;BA.debugLine="Private pan_notconn As Panel";
mostCurrent._pan_notconn = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim current_page As Int=1";
_current_page = (int) (1);
 //BA.debugLineNum = 32;BA.debugLine="Private lbl_shop_m As Label";
mostCurrent._lbl_shop_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private lbl_class_m As Label";
mostCurrent._lbl_class_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private lbl_liked_m As Label";
mostCurrent._lbl_liked_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Private lbl_info_m As Label";
mostCurrent._lbl_info_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private lbl_suport_m As Label";
mostCurrent._lbl_suport_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private lbl_marjoe_m As Label";
mostCurrent._lbl_marjoe_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private lbl_qus_m As Label";
mostCurrent._lbl_qus_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private lbl_account_m As Label";
mostCurrent._lbl_account_m = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private lbl_setting As Label";
mostCurrent._lbl_setting = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private scview_menu As ScrollView";
mostCurrent._scview_menu = new anywheresoftware.b4a.objects.ScrollViewWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private lbl_account As Label";
mostCurrent._lbl_account = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private lbl_cart As Label";
mostCurrent._lbl_cart = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private lbl_class As Label";
mostCurrent._lbl_class = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private lbl_shop As Label";
mostCurrent._lbl_shop = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private account_icon As Label";
mostCurrent._account_icon = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private cart_icon As Label";
mostCurrent._cart_icon = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private class_icon As Label";
mostCurrent._class_icon = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private shop_icon As Label";
mostCurrent._shop_icon = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Dim str_shop As String = \"https://amin-kala.ir/\"";
mostCurrent._str_shop = "https://amin-kala.ir/";
 //BA.debugLineNum = 54;BA.debugLine="Dim str_shop2 As String = \"https://amin-kala.ir/s";
mostCurrent._str_shop2 = "https://amin-kala.ir/shop";
 //BA.debugLineNum = 56;BA.debugLine="Dim str_class As String = \"https://amin-kala.ir/c";
mostCurrent._str_class = "https://amin-kala.ir/classify/";
 //BA.debugLineNum = 57;BA.debugLine="Dim str_cart As String = \"https://amin-kala.ir/ca";
mostCurrent._str_cart = "https://amin-kala.ir/cart/";
 //BA.debugLineNum = 58;BA.debugLine="Dim str_accont As String = \"https://amin-kala.ir/";
mostCurrent._str_accont = "https://amin-kala.ir/account/";
 //BA.debugLineNum = 59;BA.debugLine="Dim str_fav As String = \"https://amin-kala.ir/wis";
mostCurrent._str_fav = "https://amin-kala.ir/wishlist/";
 //BA.debugLineNum = 60;BA.debugLine="Dim str_about As String = \"https://amin-kala.ir/a";
mostCurrent._str_about = "https://amin-kala.ir/about-us/";
 //BA.debugLineNum = 61;BA.debugLine="Dim str_call As String = \"https://amin-kala.ir/co";
mostCurrent._str_call = "https://amin-kala.ir/contact-us/";
 //BA.debugLineNum = 62;BA.debugLine="Dim str_marjoee As String = \"https://amin-kala.ir";
mostCurrent._str_marjoee = "https://amin-kala.ir/marjoee/";
 //BA.debugLineNum = 63;BA.debugLine="Dim str_ques As String = \"https://amin-kala.ir/fa";
mostCurrent._str_ques = "https://amin-kala.ir/faq/";
 //BA.debugLineNum = 68;BA.debugLine="Private lbl_title As Label";
mostCurrent._lbl_title = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Dim type_app As Int";
_type_app = 0;
 //BA.debugLineNum = 70;BA.debugLine="Dim ht As HttpJob";
mostCurrent._ht = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 71;BA.debugLine="Dim phon1 As Phone";
mostCurrent._phon1 = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 72;BA.debugLine="Private lbl_share_app As Label";
mostCurrent._lbl_share_app = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Private loading As B4XLoadingIndicator";
mostCurrent._loading = new ir.taravatgroup.aminkala.b4xloadingindicator();
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _http_initial_1(int _type1) throws Exception{
String _send = "";
 //BA.debugLineNum = 122;BA.debugLine="Sub http_initial_1(type1 As Int)";
 //BA.debugLineNum = 123;BA.debugLine="ht.Initialize(\"ht\",Me)";
mostCurrent._ht._initialize(processBA,"ht",main.getObject());
 //BA.debugLineNum = 124;BA.debugLine="Dim send As String";
_send = "";
 //BA.debugLineNum = 125;BA.debugLine="send = \"username=mahdi&password=1234&div_id=\"&pho";
_send = "username=mahdi&password=1234&div_id="+mostCurrent._phon1.GetSettings("android_id")+"&sdk_ver="+BA.NumberToString(mostCurrent._phon1.getSdkVersion())+"&oprator="+mostCurrent._phon1.GetNetworkOperatorName()+"&type_app="+BA.NumberToString(_type1)+"&div_model="+mostCurrent._phon1.getModel()+"&var=0";
 //BA.debugLineNum = 126;BA.debugLine="ht.PostString(\"https://azarfadak.com/amin-kala.ph";
mostCurrent._ht._poststring("https://azarfadak.com/amin-kala.php",_send);
 //BA.debugLineNum = 127;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 134;BA.debugLine="Sub Jobdone (job As HttpJob)";
 //BA.debugLineNum = 135;BA.debugLine="If job.Success = True Then";
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 136;BA.debugLine="If job.JobName=\"ht\" Then";
if ((_job._jobname).equals("ht")) { 
 };
 }else {
 };
 //BA.debugLineNum = 145;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_account_click() throws Exception{
 //BA.debugLineNum = 203;BA.debugLine="Private Sub lbl_account_Click";
 //BA.debugLineNum = 204;BA.debugLine="current_page=4";
_current_page = (int) (4);
 //BA.debugLineNum = 205;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 206;BA.debugLine="lbl_title.Text=\"پرداخت\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("پرداخت"));
 //BA.debugLineNum = 207;BA.debugLine="WebView1.LoadUrl(str_accont)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_accont);
 //BA.debugLineNum = 208;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_account_m_click() throws Exception{
 //BA.debugLineNum = 313;BA.debugLine="Private Sub lbl_account_m_Click";
 //BA.debugLineNum = 314;BA.debugLine="current_page=4";
_current_page = (int) (4);
 //BA.debugLineNum = 315;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 316;BA.debugLine="lbl_title.Text=\"حساب کاربری\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("حساب کاربری"));
 //BA.debugLineNum = 317;BA.debugLine="WebView1.LoadUrl(str_accont)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_accont);
 //BA.debugLineNum = 318;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 319;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_cart_click() throws Exception{
 //BA.debugLineNum = 210;BA.debugLine="Private Sub lbl_cart_Click";
 //BA.debugLineNum = 211;BA.debugLine="cart_icon_Click";
_cart_icon_click();
 //BA.debugLineNum = 212;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_class_click() throws Exception{
 //BA.debugLineNum = 227;BA.debugLine="Private Sub lbl_class_Click";
 //BA.debugLineNum = 228;BA.debugLine="current_page=2";
_current_page = (int) (2);
 //BA.debugLineNum = 229;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 230;BA.debugLine="lbl_title.Text=\"دسته بندی\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("دسته بندی"));
 //BA.debugLineNum = 231;BA.debugLine="WebView1.LoadUrl(str_class)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_class);
 //BA.debugLineNum = 232;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_class_m_click() throws Exception{
 //BA.debugLineNum = 302;BA.debugLine="Private Sub lbl_class_m_Click";
 //BA.debugLineNum = 303;BA.debugLine="class_icon_Click";
_class_icon_click();
 //BA.debugLineNum = 304;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 305;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_info_m_click() throws Exception{
 //BA.debugLineNum = 286;BA.debugLine="Private Sub lbl_info_m_Click";
 //BA.debugLineNum = 287;BA.debugLine="current_page=6";
_current_page = (int) (6);
 //BA.debugLineNum = 288;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 289;BA.debugLine="lbl_title.Text=\"درباره ما\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("درباره ما"));
 //BA.debugLineNum = 290;BA.debugLine="WebView1.LoadUrl(str_about)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_about);
 //BA.debugLineNum = 291;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 292;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_liked_m_click() throws Exception{
 //BA.debugLineNum = 294;BA.debugLine="Private Sub lbl_liked_m_Click";
 //BA.debugLineNum = 295;BA.debugLine="current_page=5";
_current_page = (int) (5);
 //BA.debugLineNum = 296;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 297;BA.debugLine="lbl_title.Text=\"نشان شده ها\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("نشان شده ها"));
 //BA.debugLineNum = 298;BA.debugLine="WebView1.LoadUrl(str_fav)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_fav);
 //BA.debugLineNum = 299;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 300;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_marjoe_m_click() throws Exception{
 //BA.debugLineNum = 270;BA.debugLine="Private Sub lbl_marjoe_m_Click";
 //BA.debugLineNum = 271;BA.debugLine="current_page=8";
_current_page = (int) (8);
 //BA.debugLineNum = 272;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 273;BA.debugLine="lbl_title.Text=\"درخواست مرجوعی\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("درخواست مرجوعی"));
 //BA.debugLineNum = 274;BA.debugLine="WebView1.LoadUrl(str_marjoee)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_marjoee);
 //BA.debugLineNum = 275;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 276;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_menu_click() throws Exception{
 //BA.debugLineNum = 257;BA.debugLine="Private Sub lbl_menu_Click";
 //BA.debugLineNum = 258;BA.debugLine="pan_all_menu.Visible=True";
mostCurrent._pan_all_menu.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 259;BA.debugLine="pan_menu.SetLayout(30%x,0,70%x,100%y)";
mostCurrent._pan_menu.SetLayout(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (70),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 260;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_qus_m_click() throws Exception{
 //BA.debugLineNum = 262;BA.debugLine="Private Sub lbl_qus_m_Click";
 //BA.debugLineNum = 263;BA.debugLine="current_page=9";
_current_page = (int) (9);
 //BA.debugLineNum = 264;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 265;BA.debugLine="lbl_title.Text=\"سوالات متداول\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("سوالات متداول"));
 //BA.debugLineNum = 266;BA.debugLine="WebView1.LoadUrl(str_ques)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_ques);
 //BA.debugLineNum = 267;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 268;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_re_conn_click() throws Exception{
 //BA.debugLineNum = 416;BA.debugLine="Private Sub lbl_re_conn_Click";
 //BA.debugLineNum = 417;BA.debugLine="ProgressDialogShow(\"بارگذاری ...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("بارگذاری ..."));
 //BA.debugLineNum = 418;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 419;BA.debugLine="http_initial_1(type_app)";
_http_initial_1(_type_app);
 //BA.debugLineNum = 420;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_setting_click() throws Exception{
int _result = 0;
 //BA.debugLineNum = 445;BA.debugLine="Private Sub lbl_setting_Click";
 //BA.debugLineNum = 447;BA.debugLine="Dim result As Int";
_result = 0;
 //BA.debugLineNum = 448;BA.debugLine="result = Msgbox2(\"روش اجرای برنامه را مشخص کنید.";
_result = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("روش اجرای برنامه را مشخص کنید. برای عوض کردن روش ، برنامه را حذف و دوباره نصب کنید!"),BA.ObjectToCharSequence("روش اجرا"),"همین برنامه","","روش مرورگر",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"attention.png").getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 449;BA.debugLine="If result = DialogResponse.Positive Then";
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 452;BA.debugLine="File.WriteString(File.DirInternal,\"setstart1\",\"\"";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"setstart1","");
 //BA.debugLineNum = 453;BA.debugLine="lbl_setting.Visible=False";
mostCurrent._lbl_setting.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 454;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 }else if(_result==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
 //BA.debugLineNum = 457;BA.debugLine="File.WriteString(File.DirInternal,\"setstart2\",\"\"";
anywheresoftware.b4a.keywords.Common.File.WriteString(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"setstart2","");
 //BA.debugLineNum = 458;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 459;BA.debugLine="Activity_Create(True)";
_activity_create(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 463;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_share_app_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 491;BA.debugLine="Private Sub lbl_share_app_Click";
 //BA.debugLineNum = 493;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 494;BA.debugLine="i.Initialize(i.ACTION_SEND, \"\")";
_i.Initialize(_i.ACTION_SEND,"");
 //BA.debugLineNum = 495;BA.debugLine="i.SetType(\"text/plain\")";
_i.SetType("text/plain");
 //BA.debugLineNum = 496;BA.debugLine="i.PutExtra(\"android.intent.extra.TEXT\", \"https://";
_i.PutExtra("android.intent.extra.TEXT",(Object)("https://cafebazaar.ir/app/ir.taravatgroup.aminkala"));
 //BA.debugLineNum = 497;BA.debugLine="i.WrapAsIntentChooser(\"Select\")";
_i.WrapAsIntentChooser("Select");
 //BA.debugLineNum = 498;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 500;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_shop_click() throws Exception{
 //BA.debugLineNum = 234;BA.debugLine="Private Sub lbl_shop_Click";
 //BA.debugLineNum = 235;BA.debugLine="shop_icon_Click";
_shop_icon_click();
 //BA.debugLineNum = 236;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_shop_m_click() throws Exception{
 //BA.debugLineNum = 307;BA.debugLine="Private Sub lbl_shop_m_Click";
 //BA.debugLineNum = 308;BA.debugLine="shop_icon_Click";
_shop_icon_click();
 //BA.debugLineNum = 309;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 310;BA.debugLine="End Sub";
return "";
}
public static String  _lbl_suport_m_click() throws Exception{
 //BA.debugLineNum = 278;BA.debugLine="Private Sub lbl_suport_m_Click";
 //BA.debugLineNum = 279;BA.debugLine="current_page=7";
_current_page = (int) (7);
 //BA.debugLineNum = 280;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 281;BA.debugLine="lbl_title.Text=\"تماس با پشتیبانی\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("تماس با پشتیبانی"));
 //BA.debugLineNum = 282;BA.debugLine="WebView1.LoadUrl(str_call)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_call);
 //BA.debugLineNum = 283;BA.debugLine="pan_all_menu_Click";
_pan_all_menu_click();
 //BA.debugLineNum = 284;BA.debugLine="End Sub";
return "";
}
public static String  _openbrowser(String _link) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
anywheresoftware.b4j.object.JavaObject _jo = null;
 //BA.debugLineNum = 430;BA.debugLine="Sub OpenBrowser(Link As String)";
 //BA.debugLineNum = 432;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 434;BA.debugLine="i.Initialize(i.ACTION_VIEW,Link)";
_i.Initialize(_i.ACTION_VIEW,_link);
 //BA.debugLineNum = 436;BA.debugLine="Dim jo As JavaObject = i";
_jo = new anywheresoftware.b4j.object.JavaObject();
_jo = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_i.getObject()));
 //BA.debugLineNum = 438;BA.debugLine="jo.RunMethod(\"setPackage\", Array(\"com.android.chr";
_jo.RunMethod("setPackage",new Object[]{(Object)("com.android.chrome")});
 //BA.debugLineNum = 441;BA.debugLine="StartActivity(jo.RunMethod(\"createChooser\", Array";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,_jo.RunMethod("createChooser",new Object[]{(Object)(_i.getObject()),(Object)("برای اجرا در مرورگر ، گوگل کروم را نصب کنید و بعد امتحان نمائید.")}));
 //BA.debugLineNum = 443;BA.debugLine="End Sub";
return "";
}
public static String  _pan_all_menu_click() throws Exception{
 //BA.debugLineNum = 250;BA.debugLine="Private Sub pan_all_menu_Click";
 //BA.debugLineNum = 252;BA.debugLine="pan_menu.SetLayout(100%x,0,70%x,100%y)";
mostCurrent._pan_menu.SetLayout(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),(int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (70),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 253;BA.debugLine="pan_all_menu.Visible=False";
mostCurrent._pan_all_menu.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 255;BA.debugLine="End Sub";
return "";
}
public static String  _pan_menu_click() throws Exception{
 //BA.debugLineNum = 245;BA.debugLine="Private Sub pan_menu_Click";
 //BA.debugLineNum = 247;BA.debugLine="End Sub";
return "";
}
public static String  _pan_notconn_click() throws Exception{
 //BA.debugLineNum = 412;BA.debugLine="Private Sub pan_notconn_Click";
 //BA.debugLineNum = 414;BA.debugLine="End Sub";
return "";
}
public static String  _panel2_click() throws Exception{
 //BA.debugLineNum = 424;BA.debugLine="Private Sub Panel2_Click";
 //BA.debugLineNum = 426;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim tim As Timer";
_tim = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _shop_icon_click() throws Exception{
 //BA.debugLineNum = 238;BA.debugLine="Private Sub shop_icon_Click";
 //BA.debugLineNum = 239;BA.debugLine="current_page=1";
_current_page = (int) (1);
 //BA.debugLineNum = 240;BA.debugLine="check_conn";
_check_conn();
 //BA.debugLineNum = 241;BA.debugLine="lbl_title.Text=\"امین کالا\"";
mostCurrent._lbl_title.setText(BA.ObjectToCharSequence("امین کالا"));
 //BA.debugLineNum = 242;BA.debugLine="WebView1.LoadUrl(str_shop)";
mostCurrent._webview1.LoadUrl(mostCurrent._str_shop);
 //BA.debugLineNum = 243;BA.debugLine="End Sub";
return "";
}
public static String  _tim_tick() throws Exception{
int _res3 = 0;
 //BA.debugLineNum = 176;BA.debugLine="Sub tim_Tick";
 //BA.debugLineNum = 179;BA.debugLine="Panel1.Visible=False";
mostCurrent._panel1.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 180;BA.debugLine="tim.Enabled=False";
_tim.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 183;BA.debugLine="If (File.Exists(File.DirInternal,\"setstart2\")==Tr";
if ((anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"setstart2")==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 184;BA.debugLine="Dim res3 As Int";
_res3 = 0;
 //BA.debugLineNum = 185;BA.debugLine="res3 = Msgbox2(\"برای اجرا در مرورگر ، گوگل کروم";
_res3 = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("برای اجرا در مرورگر ، گوگل کروم را نصب کنید و بعد امتحان نمائید."),BA.ObjectToCharSequence("توجه!"),"خروج","","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"attention.png").getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 186;BA.debugLine="If res3 = DialogResponse.Positive Then";
if (_res3==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 187;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 }else {
 //BA.debugLineNum = 189;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 };
 };
 //BA.debugLineNum = 194;BA.debugLine="End Sub";
return "";
}
public static String  _webview1_pagefinished(String _url) throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Sub WebView1_PageFinished (Url As String)";
 //BA.debugLineNum = 119;BA.debugLine="loading.Hide";
mostCurrent._loading._hide /*String*/ ();
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
}
