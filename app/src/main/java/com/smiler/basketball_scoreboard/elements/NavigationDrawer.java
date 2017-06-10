package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.smiler.basketball_scoreboard.R;

public class NavigationDrawer {
    private Drawer.Result drawer;
    private Activity activity;

    public NavigationDrawer(Activity activity) {
        this.activity = activity;
        AccountHeader.Result drawerHeader = createDrawerHeader();
        drawer = new Drawer()
                .withActivity(activity)
                .withTranslucentStatusBar(true)
                .withFullscreen(true)
                .withDrawerWidthPx(activity.getResources().getDimensionPixelSize(R.dimen.drawer_width))
                .withAccountHeader(drawerHeader)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(initDrawerItems())
                .withOnDrawerItemClickListener((Drawer.OnDrawerItemClickListener) activity)
                .build();
    }

    private AccountHeader.Result createDrawerHeader() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new AccountHeader()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.drawer_header)
                .build();
    }

    private IDrawerItem[] initDrawerItems() {
        Resources resources = activity.getResources();
        return new IDrawerItem[]{
                new SecondaryDrawerItem().withName(R.string.action_new_game).withIcon(resources.getDrawable(R.drawable.ic_action_replay)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_results).withIcon(resources.getDrawable(R.drawable.ic_action_storage)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_profiles).withIcon(resources.getDrawable(R.drawable.ic_action_group)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(resources.getDrawable(R.drawable.ic_action_settings)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_share).withIcon(resources.getDrawable(R.drawable.ic_action_share)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_help).withIcon(resources.getDrawable(R.drawable.ic_action_about)).withCheckable(false),
        };
    }

    public boolean isOpen() {
        return drawer != null && drawer.isDrawerOpen();
    }

    public void open() {
        if (drawer != null) {
            drawer.openDrawer();
        }
    }

    public void close() {
        if (drawer != null) {
            drawer.closeDrawer();
        }
    }

}
