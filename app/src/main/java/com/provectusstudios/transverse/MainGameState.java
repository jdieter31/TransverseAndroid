package com.provectusstudios.transverse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.Matrix;
import android.support.v4.view.MotionEventCompat;

import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyV4VCAd;
import com.jirbo.adcolony.AdColonyV4VCListener;
import com.jirbo.adcolony.AdColonyV4VCReward;
import com.jirbo.adcolony.AdColonyVideoAd;
import com.unity3d.ads.android.IUnityAdsListener;
import com.unity3d.ads.android.UnityAds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class MainGameState implements GameState, AdColonyV4VCListener, IUnityAdsListener {

    private boolean refreshing = false;

    //Prevent concurrency errors by changing this boolean rather than calling loss method
    private boolean scheduledLoss = false;
    private boolean scheduledRestart = false;
    private boolean scheduledEndGame = false;
    private boolean scheduledSecondChance = false;
    private boolean scheduledControlSwitch = false;

    private static String LEADERBOARD_ID = "CgkIjb-hrowBEAIQAQ";

    private MainRenderer mainRenderer;

    private SolidRenderType currentRenderer;

    private SolidRenderType lineRenderType;
    private SolidRenderType greyRenderType;
    private SolidRenderType titleRenderType;

    private int highScore = 0;

    private int leftPointer;
    private int rightPointer;

    private int score = 0;
    private Text scoreText;

    private boolean leftDown;
    private boolean rightDown;

    private Circle leftCircle;
    private Circle rightCircle;

    private float[] verticalTranslate;

    private Path leftPath = new Path();
    private Path rightPath = new Path();

    private float[] viewProjectionMatrix;
    private float width;
    private float height;

    private float verticalChange = 0;

    private boolean started;

    private long lastMoveCalc;
    private float speed = 150;

    private boolean circlesInView = true;
    private boolean wallsInView = true;

    private float endCurrentGenerate = 0;

    private List<Section> sectionsInView = new ArrayList<>();

    private volatile float leftX;
    private volatile float leftY;
    private volatile float rightX;
    private volatile float rightY;
    private float lastLeftX;
    private float lastLeftY;
    private float lastRightX;
    private float lastRightY;
    private float lastVerticalChange;

    private boolean hadSecondChance = false;
    private boolean inSecondChanceMenu = false;

    private boolean startingSecondChance = false;
    private RoundedRectangle startingSecondChanceRectangle;
    private Text secondChanceTapAndHoldText;
    private Text toRetryText;
    private Circle leftSecondChanceCircle;
    private Circle rightSecondChanceCircle;

    private RoundedRectangle secondChanceBox;
    private RoundedRectangle endGameButton;
    private Text endText;
    private Text gameText;
    private RoundedRectangle secondChanceButton;
    private Text secondChanceText;
    private Text watchVideoText;
    private SolidRenderType darkRedRenderType;

    private boolean animatingColorChange;
    private long timeOfChange;
    private SolidRenderType previousRenderType;
    private SolidRenderType previousBackgroundRenderType;
    private SolidRenderType mixRenderType;
    private SolidRenderType mixBackgroundRenderType;

    private SolidRenderType defaultBackgroundRenderer;

    private Section currentSection;

    private RenderType redRenderType;
    private boolean inLossMenu;
    private boolean animatingLoss;
    private long timeOfLoss;
    private RoundedRectangle loseScoreRectangle;
    private Text loseScoreNumberText;
    private Text loseScoreText;
    private RoundedRectangle highScoreBox;
    private Text highScoreText;
    private RoundedRectangle retryRectangle;
    private Text retryText;

    private Rectangle backgroundRectangle;

    private Rectangle leftWall;
    private Rectangle centerDivider;
    private Rectangle rightWall;

    private SolidRenderType backgroundRenderType;
    private RenderType scoreRectangleRenderType;

    private Random random = new Random();

    private long lastSpeedCalculation = -1;

    private RoundedRectangle titleRectangle;
    private Text titleText;
    private RoundedRectangle tapToStartRectangle;
    private Text tapAndHoldText;
    private Text toStartText;

    private boolean titleInView = true;

    private float titleAlpha = 1;
    private long lastTitleCalculation = -1;

    private float sectionToPass = 0;
    private float nextSectionToPass = 0;

    private RoundedRectangle titleHighScoreBox;
    private Text titleHighScoreText;

    private RoundedRectangle leaderboardBox;
    private Image leaderboardImage;

    private RoundedRectangle achievementBox;
    private Image achievementImage;

    private RoundedRectangle switchControlSchemeBox;
    private Text switchControlsText;

    private Rectangle rightDragRectangle;
    private Rectangle leftDragRectangle;

    private Text leftArrowsLeftText;
    private Text leftArrowsRightText;
    private Text rightArrowsLeftText;
    private Text rightArrowsRightText;

    private Circle outerDragRightCircle;
    private Circle outerDragLeftCircle;

    private Circle dragRightCircle;
    private Circle dragLeftCircle;

    private Rectangle dragInfoBox = new Rectangle();
    private Text dragAndHoldOnBothSidesToStart;

    private float dragOffset = 0;

    private boolean purchasedSecondChance = false;

    private RoundedRectangle purchaseSecondChanceBox;
    private Text buyNoAdsAndText;
    private Text secondChancesText;

    private RoundedRectangle loseAchievementBox;
    private Image loseAchievementImage;
    private RoundedRectangle loseLeaderboardBox;
    private Image loseLeaderboardImage;
    private RoundedRectangle loseShareBox;
    private Text shareText;

    private GoogleApiClient gClient;

    public static final String zeroAchievement = "CgkIjb-hrowBEAIQBw";
    public static final String tenAchievement = "CgkIjb-hrowBEAIQAg";
    public static final String twentyAchievement = "CgkIjb-hrowBEAIQAw";
    public static final String thirtyAchievement = "CgkIjb-hrowBEAIQBA";
    public static final String fourtyAchievement = "CgkIjb-hrowBEAIQBQ";
    public static final String fiftyAchievement = "CgkIjb-hrowBEAIQBg";
    public static final String hundredAchievement = "CgkIjb-hrowBEAIQCA";

    private int gamesPlayedSinceAd = 0;
    private boolean initialAd = true;
    private static final int initialAds = 2;
    private static final int gamesPerAd = 5;

    private boolean dragControlScheme = false;
    private boolean showIndicator = false;

    private Circle fbCircle;
    private Image fbImage;

    private Circle twitterCircle;
    private Image twitterImage;

    private SolidRenderType fbRenderType;
    private SolidRenderType twitterRenderType;

    public MainGameState(MainRenderer mainRenderer) {

        AdColony.addV4VCListener(this);

        UnityAds.setListener(this);

        gClient = ((MainActivity) mainRenderer.getContext()).getGClient();

        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        tracker.setScreenName("Main Menu");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());


        this.mainRenderer = mainRenderer;
        readHighScore();
        SharedPreferences pref = ((Activity) mainRenderer.getContext()).getPreferences(Context.MODE_PRIVATE);
        dragControlScheme = pref.getBoolean("dragControlScheme", false);
        showIndicator = pref.getBoolean("showIndicator", false);
        greyRenderType = new SolidRenderType();
        greyRenderType.setAlpha(1);
        greyRenderType.setColor(.4f, .4f, .4f);
        lineRenderType = new SolidRenderType();
        lineRenderType.setAlpha(1);
        lineRenderType.setColor(.322f, .808f, 1f);
        verticalTranslate = new float[16];
        Matrix.setIdentityM(verticalTranslate, 0);
        leftPath.setWidth(10f);
        rightPath.setWidth(10f);
        defaultBackgroundRenderer = backgroundRenderType = new SolidRenderType();
        backgroundRenderType.setColor(.95f, .95f, .95f);
        scoreRectangleRenderType = new SolidRenderType();
        ((SolidRenderType) scoreRectangleRenderType).setColor(1f,1f,1f);
        redRenderType = new SolidRenderType();
        ((SolidRenderType) redRenderType).setColor(1f, .3f, .3f);
        redRenderType.setAlpha(1f);
        darkRedRenderType = new SolidRenderType();
        darkRedRenderType.setColor(.784f, .137f, .263f);
        darkRedRenderType.setAlpha(1f);
        titleRenderType = new SolidRenderType();
        titleRenderType.setColor(.204f, .553f, .686f);
        titleRenderType.setAlpha(1f);
        fbRenderType = new SolidRenderType();
        fbRenderType.setColor(.231f, .349f, .596f);
        fbRenderType.setAlpha(1);
        twitterRenderType = new SolidRenderType();
        twitterRenderType.setColor(.333f, .675f, .933f);
        twitterRenderType.setAlpha(1);
        currentRenderer = greyRenderType;
    }

    private void readHighScore() {
        SharedPreferences pref = ((Activity) mainRenderer.getContext()).getPreferences(Context.MODE_PRIVATE);
        highScore = pref.getInt(mainRenderer.getContext().getString(R.string.saved_high_score), 0);
    }

    @Override
    public void handleTouchEvent(MotionEvent event) {
        float density = mainRenderer.getContext().getResources().getDisplayMetrics().density;
        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int pointerIndex = MotionEventCompat.getActionIndex(event);
                int pointerID = MotionEventCompat.getPointerId(event, pointerIndex);
                float x = MotionEventCompat.getX(event, pointerIndex);
                float y = MotionEventCompat.getY(event, pointerIndex);
                float dpX = x/density;
                float dpY = y/density;
                if (!started) {
                    if (!dragControlScheme && !rightDown && rightCircle.containsPoint(dpX, dpY)) {
                        rightDown = true;
                        rightPointer = pointerID;
                        rightX = dpX;
                        rightY = dpY;
                        lastRightX = rightX;
                        lastRightY = rightY;
                    } else if (!dragControlScheme && !leftDown && leftCircle.containsPoint(dpX, dpY)) {
                        leftX = dpX;
                        leftY = dpY;
                        lastLeftX = leftX;
                        lastLeftY = leftY;
                        leftDown = true;
                        leftPointer = pointerID;
                    } else if (leaderboardBox.containsPoint(dpX, dpY)) {
                        if (gClient != null && gClient.isConnected()) {
                            Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                            tracker.setScreenName("Leaderboard");
                            tracker.send(new HitBuilders.ScreenViewBuilder().build());
                            ((Activity) mainRenderer.getContext()).startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gClient,
                                    LEADERBOARD_ID), 0);
                        }
                    } else if (achievementBox.containsPoint(dpX, dpY)) {
                        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                        tracker.setScreenName("Achievements");
                        tracker.send(new HitBuilders.ScreenViewBuilder().build());
                        if (gClient != null && gClient.isConnected()) {
                            ((Activity) mainRenderer.getContext()).startActivityForResult(Games.Achievements.getAchievementsIntent(gClient), 0);
                        }
                    } else if (switchControlSchemeBox.containsPoint(dpX, dpY)) {
                        scheduledControlSwitch = true;
                    } else if (!purchasedSecondChance && purchaseSecondChanceBox != null && purchaseSecondChanceBox.containsPoint(dpX, dpY)) {
                        ((MainActivity) mainRenderer.getContext()).purchaseNoAds();
                    } else if (fbCircle.containsPoint(dpX, dpY)) {
                        mainRenderer.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/provectusstudios")));
                    } else if (twitterCircle.containsPoint(dpX, dpY)) {
                        mainRenderer.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/provectustudios")));
                    } else if (dragControlScheme && dpX > 15 && dpX < width/2 - 7.5f) {
                        leftX = dpX;
                        leftY = height/2;
                        lastLeftX = leftX;
                        lastLeftY = leftY;
                        leftDown = true;
                        leftPointer = pointerID;
                    } else if (dragControlScheme && dpX > width/2 + 7.5f && dpX < width - 15) {
                        rightDown = true;
                        rightPointer = pointerID;
                        rightX = dpX;
                        rightY = height/2;
                        lastRightX = rightX;
                        lastRightY = height/2;
                    }

                    if (rightDown && leftDown) {
                        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                        tracker.setScreenName("Gameplay");
                        tracker.send(new HitBuilders.ScreenViewBuilder().build());
                        started = true;
                        lastMoveCalc = System.currentTimeMillis();
                    }

                }
                if (startingSecondChance) {
                    if (!dragControlScheme && !rightDown && rightSecondChanceCircle.containsPoint(dpX, dpY + verticalChange)) {
                        rightDown = true;
                        rightPointer = pointerID;
                        rightX = dpX;
                        rightY = dpY;
                        lastRightX = rightX;
                        lastRightY = rightY;
                    } else if (!dragControlScheme && !leftDown && leftSecondChanceCircle.containsPoint(dpX, dpY + verticalChange)) {
                        leftX = dpX;
                        leftY = dpY;
                        lastLeftX = leftX;
                        lastLeftY = leftY;
                        leftDown = true;
                        leftPointer = pointerID;
                    } else if (dragControlScheme && dpX > 15 && dpX < width/2 - 7.5f) {
                        leftX = dpX;
                        leftY = height/2;
                        lastLeftX = leftX;
                        lastLeftY = leftY;
                        leftDown = true;
                        leftPointer = pointerID;
                    } else if (dragControlScheme && dpX > width/2 + 7.5f && dpX < width - 15) {
                        rightDown = true;
                        rightPointer = pointerID;
                        rightX = dpX;
                        rightY = height/2;
                        lastRightX = rightX;
                        lastRightY = height/2;
                    }

                    if (rightDown && leftDown) {
                        leftPath = new Path();
                        leftPath.setWidth(10f);
                        rightPath = new Path();
                        rightPath.setWidth(10f);
                        startingSecondChance = false;
                        scheduledLoss = false;
                        started = true;
                        lastMoveCalc = System.currentTimeMillis();
                    }
                }
                if (inLossMenu) {
                    if (retryRectangle.containsPoint(dpX, dpY)) {
                        scheduledRestart = true;
                    } else if (loseShareBox.containsPoint(dpX, dpY)) {
                        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                        tracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Social")
                                .setAction("Share")
                                .setNonInteraction(true)
                                .build());
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "I scored " + score + " points on #Transverse! Can you beat it? https://play.google.com/store/apps/details?id=com.provectusstudios.transverse");
                        sendIntent.setType("text/plain");
                        mainRenderer.getContext().startActivity(sendIntent);
                    } else if (loseLeaderboardBox.containsPoint(dpX, dpY)) {
                        if (gClient != null && gClient.isConnected()) {
                            Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                            tracker.setScreenName("Leaderboard");
                            tracker.send(new HitBuilders.ScreenViewBuilder().build());
                            ((Activity) mainRenderer.getContext()).startActivityForResult(Games.Leaderboards.getLeaderboardIntent(gClient,
                                    LEADERBOARD_ID), 0);
                        }
                    } else if (loseAchievementBox.containsPoint(dpX, dpY)) {
                        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                        tracker.setScreenName("Achievements");
                        tracker.send(new HitBuilders.ScreenViewBuilder().build());
                        if (gClient != null && gClient.isConnected()) {
                            ((Activity) mainRenderer.getContext()).startActivityForResult(Games.Achievements.getAchievementsIntent(gClient), 0);
                        }
                    }
                }
                if (inSecondChanceMenu) {
                    if (endGameButton.containsPoint(dpX, dpY)) {
                        inSecondChanceMenu = false;
                        scheduledEndGame = true;
                    } else if (secondChanceButton.containsPoint(dpX, dpY)) {
                        if (purchasedSecondChance)  {
                            scheduledSecondChance = true;
                        } else if (Math.random() > .5f) {
                            AdColonyV4VCAd ad = new AdColonyV4VCAd(MainActivity.retry_zone);
                            ad.show();
                        } else {
                            try {
                                if (UnityAds.setZone("rewardedVideo") && UnityAds.canShow()) {
                                    UnityAds.show();
                                }
                            } catch (IllegalStateException e) {
                                Log.e("Transverse", e.getMessage());
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = MotionEventCompat.getActionIndex(event);
                pointerID = MotionEventCompat.getPointerId(event, pointerIndex);
                if (!started) {
                    if (rightDown && pointerID == rightPointer) {
                        rightDown = false;
                        rightPointer = 0;
                    }
                    if (leftDown && pointerID == leftPointer) {
                        leftDown = false;
                        leftPointer = 0;
                    }
                } else if (startingSecondChance) {
                    if (rightDown && pointerID == rightPointer) {
                        rightDown = false;
                        rightPointer = 0;
                    }
                    if (leftDown && pointerID == leftPointer) {
                        leftDown = false;
                        leftPointer = 0;
                    }
                } else if (!inLossMenu) {
                    if (pointerID == leftPointer || pointerID == rightPointer) {
                        //Use boolean to prevent concurrency errors
                        scheduledLoss = true;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (started) {
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        pointerID = MotionEventCompat.getPointerId(event, i);
                        x = MotionEventCompat.getX(event, i);
                        y = MotionEventCompat.getY(event, i);
                        dpX = x/density;
                        dpY = y/density;
                        if (pointerID == leftPointer) {
                            leftX = dpX;
                            if (!dragControlScheme) {
                                leftY = dpY;
                            }
                        } else if (pointerID == rightPointer) {
                            rightX = dpX;
                            if (!dragControlScheme) {
                                rightY = dpY;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void updateHighScore(int score) {
        highScore = score;
        SharedPreferences pref = ((Activity) mainRenderer.getContext()).getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt(mainRenderer.getContext().getString(R.string.saved_high_score), highScore);
        edit.apply();
    }

    private void handleLoss() {
        if (!hadSecondChance) {
            createSecondChanceMenu();
        } else {
            finishGame();
        }
        MediaPlayer mp = MediaPlayer.create(mainRenderer.getContext().getApplicationContext(), R.raw.loss);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
            }

        });
        mp.start();
    }

    private void createSecondChance() {
        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        tracker.setScreenName("Gameplay");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        if (currentSection != null) {
            currentSection.empty();
            if (height/2 + verticalChange <= sectionToPass) {
                sectionsInView.get(sectionsInView.indexOf(currentSection) + 1).empty();
            }
        }
        rightDown = false;
        leftDown = false;
        leftPath = new Path();
        rightPath = new Path();

        leftSecondChanceCircle = new Circle();
        leftSecondChanceCircle.setPrecision(360);
        leftSecondChanceCircle.setRadius(height/10);
        rightSecondChanceCircle = new Circle();
        rightSecondChanceCircle.setPrecision(360);
        rightSecondChanceCircle.setRadius(height/10);
        leftSecondChanceCircle.setCenter(width/4, height/2 + verticalChange, 0);
        rightSecondChanceCircle.setCenter(3*width/4, height/2 + verticalChange, 0);
        leftSecondChanceCircle.refresh();
        rightSecondChanceCircle.refresh();

        dragLeftCircle = new Circle();
        dragLeftCircle.setPrecision(60);
        dragLeftCircle.setRadius(height/60);
        dragRightCircle = new Circle();
        dragRightCircle.setPrecision(60);
        dragRightCircle.setRadius(height/60);
        dragLeftCircle.setCenter(width/4, height/2 + verticalChange, 0);
        dragRightCircle.setCenter(3*width/4, height/2 + verticalChange, 0);
        dragLeftCircle.refresh();
        dragRightCircle.refresh();

        if (!dragControlScheme) {
            startingSecondChanceRectangle = new RoundedRectangle();
            startingSecondChanceRectangle.setWidth(2 * width / 3);
            startingSecondChanceRectangle.setCenter(width / 2, height / 2 + verticalChange, 0);
            startingSecondChanceRectangle.setHeight(height / 4);
            startingSecondChanceRectangle.setCornerRadius(10f);
            startingSecondChanceRectangle.setPrecision(60);
            startingSecondChanceRectangle.refresh();
            secondChanceTapAndHoldText = new Text();
            secondChanceTapAndHoldText.setFont("FFF Forward");
            secondChanceTapAndHoldText.setText("Tap and hold");
            secondChanceTapAndHoldText.setTextSize((height / 4) / 3);
            secondChanceTapAndHoldText.setOrigin(width / 2 - secondChanceTapAndHoldText.getWidth() / 2, height / 2 - (height / 4) / 3 + verticalChange, 0);
            secondChanceTapAndHoldText.refresh();
            toRetryText = new Text();
            toRetryText.setFont("FFF Forward");
            toRetryText.setText("to continue!");
            toRetryText.setTextSize((height / 4) / 3);
            toRetryText.setOrigin(width / 2 - toRetryText.getWidth() / 2, height / 2 + verticalChange, 0);
            toRetryText.refresh();
        } else {
            outerDragLeftCircle = new Circle();
            outerDragLeftCircle.setPrecision(360);
            outerDragLeftCircle.setRadius(height/15);
            outerDragRightCircle = new Circle();
            outerDragRightCircle.setPrecision(360);
            outerDragRightCircle.setRadius(height/15);
            outerDragLeftCircle.setCenter(width/4, height/2 + verticalChange, 0);
            outerDragRightCircle.setCenter(3*width/4, height/2 + verticalChange, 0);
            outerDragLeftCircle.refresh();
            outerDragRightCircle.refresh();
            rightDragRectangle = new Rectangle();
            rightDragRectangle.setOrigin(width/2 + 7.5f, height/2 + verticalChange, 0);
            rightDragRectangle.setHeight(height / 8);
            rightDragRectangle.setWidth(width/2 - 22.5f);
            rightDragRectangle.refresh();
            leftDragRectangle = new Rectangle();
            leftDragRectangle.setOrigin(15f, height/2 + verticalChange, 0);
            leftDragRectangle.setHeight(height / 8);
            leftDragRectangle.setWidth(width/2 - 22.5f);
            leftDragRectangle.refresh();

            leftArrowsLeftText = new Text();
            leftArrowsLeftText.setText("<<<");
            leftArrowsLeftText.setFont("FFF Forward");
            leftArrowsLeftText.setTextSize(height/12);
            leftArrowsLeftText.setOrigin(15 + (width - 45)/8 - leftArrowsLeftText.getWidth()/2, 9 * height/ 16 - height/24 + verticalChange, 0);
            leftArrowsLeftText.refresh();

            leftArrowsRightText = new Text();
            leftArrowsRightText.setText(">>>");
            leftArrowsRightText.setFont("FFF Forward");
            leftArrowsRightText.setTextSize(height/12);
            leftArrowsRightText.setOrigin(15 + 3*(width - 45)/8 - leftArrowsRightText.getWidth()/2, 9 * height/ 16 - height/24 + verticalChange, 0);
            leftArrowsRightText.refresh();

            rightArrowsLeftText = new Text();
            rightArrowsLeftText.setText("<<<");
            rightArrowsLeftText.setFont("FFF Forward");
            rightArrowsLeftText.setTextSize(height/12);
            rightArrowsLeftText.setOrigin(30 + 5*(width - 45)/8 - rightArrowsLeftText.getWidth()/2, 9 * height/ 16 - height/24 + verticalChange, 0);
            rightArrowsLeftText.refresh();

            rightArrowsRightText = new Text();
            rightArrowsRightText.setText(">>>");
            rightArrowsRightText.setFont("FFF Forward");
            rightArrowsRightText.setTextSize(height/12);
            rightArrowsRightText.setOrigin(30 + 7*(width - 45)/8 - rightArrowsRightText.getWidth()/2, 9 * height/16 - height/24 + verticalChange, 0);
            rightArrowsRightText.refresh();

            dragInfoBox = new Rectangle();
            dragInfoBox.setOrigin(15, height/2 - height/12 - height/11 + verticalChange, 0);
            dragInfoBox.setWidth(width - 30);
            dragInfoBox.setHeight(height/12);
            dragInfoBox.refresh();

            dragAndHoldOnBothSidesToStart = new Text();
            dragAndHoldOnBothSidesToStart.setText("Drag and hold on both sides to continue!");
            dragAndHoldOnBothSidesToStart.setFont("FFF Forward");
            dragAndHoldOnBothSidesToStart.setTextSize(height/16);
            dragAndHoldOnBothSidesToStart.setOrigin(width/2 - dragAndHoldOnBothSidesToStart.getWidth()/2, height/2 - height/11 - height/24 - height/32 + verticalChange, 0);
            dragAndHoldOnBothSidesToStart.refresh();

            dragOffset = verticalChange;
        }

        scheduledSecondChance = false;
        inSecondChanceMenu = false;
        hadSecondChance = true;
        startingSecondChance = true;
    }


    private void createSecondChanceMenu() {
        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        tracker.setScreenName("Second Chance Menu");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        secondChanceBox = new RoundedRectangle();
        secondChanceBox.setWidth(2*width/3);
        secondChanceBox.setCenter(width/2, height/2, 0);
        secondChanceBox.setHeight(height/3);
        secondChanceBox.setCornerRadius(10f);
        secondChanceBox.setPrecision(60);
        secondChanceBox.refresh();

        endGameButton = new RoundedRectangle();
        endGameButton.setWidth(2 * width / 9);
        endGameButton.setCenter(width/2 - width/3 + width/9 + width/36, height/2, 0);
        endGameButton.setHeight(height/4);
        endGameButton.setCornerRadius(10f);
        endGameButton.setPrecision(60);
        endGameButton.refresh();

        endText = new Text();
        endText.setFont("FFF Forward");
        endText.setText("End");
        endText.setTextSize(height/10);
        endText.setOrigin(width/2 - width/3 + width/9 + width/36 - endText.getWidth()/2, height/2 - height/10, 0);
        endText.refresh();

        gameText = new Text();
        gameText.setFont("FFF Forward");
        gameText.setText("Game");
        gameText.setTextSize(height/10);
        gameText.setOrigin(width/2 - width/3 + width/9 + width/36 - gameText.getWidth()/2 ,height/2, 0);
        gameText.refresh();

        secondChanceButton = new RoundedRectangle();
        secondChanceButton.setWidth(13 * width / 36);
        secondChanceButton.setCenter(width/2 - width/3 + width/36 + 2*width/9 + width/36 + 13 * width / 72, height/2, 0);
        secondChanceButton.setHeight(height/4);
        secondChanceButton.setCornerRadius(10f);
        secondChanceButton.setPrecision(60);
        secondChanceButton.refresh();

        secondChanceText = new Text();
        secondChanceText.setFont("FFF Forward");
        secondChanceText.setText("Second Chance");
        secondChanceText.setTextSize(height/12);
        if (purchasedSecondChance) {
            secondChanceText.setOrigin(width/2 - width/3 + width/36 + 2*width/9 + width/36 + 13 * width / 72 - secondChanceText.getWidth()/2, height/2 - height/24, 0);
        } else {
            secondChanceText.setOrigin(width / 2 - width / 3 + width / 36 + 2 * width / 9 + width / 36 + 13 * width / 72 - secondChanceText.getWidth() / 2, height / 2 - height / 12, 0);
        }
        secondChanceText.refresh();
        if (!purchasedSecondChance) {
            watchVideoText = new Text();
            watchVideoText.setFont("FFF Forward");
            watchVideoText.setText("Watch Video");
            watchVideoText.setTextSize(height / 12);
            watchVideoText.setOrigin(width / 2 - width / 3 + width / 36 + 2 * width / 9 + width / 36 + 13 * width / 72 - watchVideoText.getWidth() / 2, height / 2, 0);
            watchVideoText.refresh();
        }

        hadSecondChance = true;
        inSecondChanceMenu = true;
    }

    private void unlockAchievements() {
        if (gClient != null && gClient.isConnected()) {
            if (score == 0) {
                Games.Achievements.unlock(gClient, zeroAchievement);
            }
            if (score >= 10) {
                Games.Achievements.unlock(gClient, tenAchievement);
            }
            if (score >= 20) {
                Games.Achievements.unlock(gClient, twentyAchievement);
            }
            if (score >= 30) {
                Games.Achievements.unlock(gClient, thirtyAchievement);
            }
            if (score >= 40) {
                Games.Achievements.unlock(gClient, fourtyAchievement);
            }
            if (score >= 50) {
                Games.Achievements.unlock(gClient, fiftyAchievement);
            }
            if (score >= 100) {
                Games.Achievements.unlock(gClient, hundredAchievement);
            }
        }
    }

    private void finishGame() {
        if (gClient != null && gClient.isConnected()) {
            Games.Leaderboards.submitScore(gClient, LEADERBOARD_ID, score);
        }
        if (score > highScore) {
            updateHighScore(score);
        }
        unlockAchievements();
        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        tracker.setScreenName("Loss Menu");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Game")
                .setAction("Finished Game")
                .setNonInteraction(true).setValue(score)
                .build());

        timeOfLoss = System.currentTimeMillis();
        loseScoreRectangle = new RoundedRectangle();
        loseScoreRectangle.setCenter(width/4, height/2, 0);
        loseScoreRectangle.setWidth(width / 3);
        loseScoreRectangle.setHeight(2 * height / 3);
        loseScoreRectangle.setCornerRadius(10f);
        loseScoreRectangle.setPrecision(60);
        loseScoreRectangle.refresh();
        loseScoreNumberText = new Text();
        loseScoreNumberText.setFont("FFF Forward");
        loseScoreNumberText.setTextSize(height / 3);
        loseScoreNumberText.setText("" + score);
        loseScoreNumberText.setOrigin(width / 4 - loseScoreNumberText.getWidth() / 2, height / 2 - 2 * height / 9, 0);
        loseScoreNumberText.refresh();
        loseScoreText = new Text();
        loseScoreText.setFont("FFF Forward");
        loseScoreText.setText("Points");
        loseScoreText.setTextSize((2*height/3)/5);
        loseScoreText.setOrigin(width/4 - loseScoreText.getWidth()/2, height/2 + height / 8, 0);
        loseScoreText.refresh();
        highScoreBox = new RoundedRectangle();
        highScoreBox.setCenter(17*width/24, 4*height/15, 0);
        highScoreBox.setCornerRadius(10f);
        highScoreBox.setHeight(height/5);
        highScoreBox.setWidth(3*width/7);
        highScoreBox.setPrecision(60);
        highScoreBox.refresh();
        highScoreText = new Text();
        highScoreText.setFont("FFF Forward");
        highScoreText.setText("Best: " + highScore);
        highScoreText.setTextSize((2*height/3)/5);
        highScoreText.setOrigin(17*width/24 - highScoreText.getWidth()/2, 4*height/15 - ((height/3)/5), 0);
        highScoreText.refresh();
        retryRectangle = new RoundedRectangle();
        retryRectangle.setCenter(17*width/24, 11*height/15, 0);
        retryRectangle.setCornerRadius(10f);
        retryRectangle.setHeight(height/5);
        retryRectangle.setWidth(3*width/7);
        retryRectangle.setPrecision(60);
        retryRectangle.refresh();
        retryText = new Text();
        retryText.setFont("FFF Forward");
        retryText.setText("Restart");
        retryText.setTextSize((2*height/3)/5);
        retryText.setOrigin(17*width/24 - highScoreText.getWidth()/2, 11*height/15 - ((height/3)/5), 0);
        retryText.refresh();

        loseShareBox = new RoundedRectangle();
        loseShareBox.setHeight(height/5);
        loseShareBox.setWidth(width/4);
        loseShareBox.setCenter(17*width/24, height/2, 0);
        loseShareBox.setPrecision(60);
        loseShareBox.setCornerRadius(10f);
        loseShareBox.refresh();
        shareText = new Text();
        shareText.setFont("FFF Forward");
        shareText.setText("Share");
        shareText.setTextSize((2*height/3)/5);
        shareText.setOrigin(17*width/24 - shareText.getWidth()/2, height/2 - (height/3)/5, 0);
        shareText.refresh();

        loseLeaderboardBox = new RoundedRectangle();
        loseLeaderboardBox.setHeight(height/5);
        loseLeaderboardBox.setWidth(height/5);
        loseLeaderboardBox.setCenter(17*width/24 - width/8 - width/50 - height/10, height/2, 0);
        loseLeaderboardBox.setPrecision(60);
        loseLeaderboardBox.setCornerRadius(10f);
        loseLeaderboardBox.refresh();

        float leaderboardImageHeight = 5*(height/5)/8;
        float leaderboardImageWidth = leaderboardImageHeight * (196f/210f);
        float leaderboardCenterX = 17*width/24 - width/8 - width/50 - height/10;
        loseLeaderboardImage = new Image();
        loseLeaderboardImage.setTextureHandle(Textures.leaderboardTexture);
        loseLeaderboardImage.setVertices(new float[] {
                leaderboardCenterX - leaderboardImageWidth/2, height/2 - leaderboardImageHeight/2, 0,
                leaderboardCenterX - leaderboardImageWidth/2, height/2 + leaderboardImageHeight/2, 0,
                leaderboardCenterX + leaderboardImageWidth/2, height/2 + leaderboardImageHeight/2, 0,
                leaderboardCenterX + leaderboardImageWidth/2, height/2 - leaderboardImageHeight/2, 0
        });
        loseLeaderboardImage.setDrawOrder(new short[] {
                0,1,2,0,2,3
        });
        loseLeaderboardImage.setUVCoordinates(new float[] {
                0,0,
                0,1,
                1,1,
                1,0
        });
        loseLeaderboardImage.refresh();


        loseAchievementBox = new RoundedRectangle();
        loseAchievementBox.setHeight(height/5);
        loseAchievementBox.setWidth(height/5);
        loseAchievementBox.setCenter(17*width/24 + width/8 + width/50 + height/10, height/2, 0);
        loseAchievementBox.setPrecision(60);
        loseAchievementBox.setCornerRadius(10f);
        loseAchievementBox.refresh();

        float achievementImageWidth = 5*(height/5)/8;
        float achievementImageHeight = achievementImageWidth * (215f/256f);
        float achievementCenterX = 17*width/24 + width/8 + width/50 + height/10;
        loseAchievementImage= new Image();
        loseAchievementImage.setTextureHandle(Textures.trophyTexture);
        loseAchievementImage.setVertices(new float[] {
                achievementCenterX - achievementImageWidth/2, height/2 - achievementImageHeight/2, 0,
                achievementCenterX - achievementImageWidth/2, height/2 + achievementImageHeight/2, 0,
                achievementCenterX + achievementImageWidth/2, height/2 + achievementImageHeight/2, 0,
                achievementCenterX + achievementImageWidth/2, height/2 - achievementImageHeight/2, 0
        });
        loseAchievementImage.setDrawOrder(new short[] {
                0,1,2,0,2,3
        });
        loseAchievementImage.setUVCoordinates(new float[] {
                0,0,
                0,1,
                1,1,
                1,0
        });
        loseAchievementImage.refresh();

        inLossMenu = true;
        animatingLoss = true;
        scheduledEndGame = false;
    }

    private void handleSectionTouch() {

        //Read all position variables into stable variable so they don't change during computation
        float leftXStable = leftX;
        float leftYStable = leftY;
        float rightXStable = rightX;
        float rightYStable = rightY;

        boolean leftPast = false;
        Path.Point newLeftPoint = new Path.Point();

        newLeftPoint.x = leftXStable;
        newLeftPoint.y = leftYStable + verticalChange;
        leftPath.addTopPoint(newLeftPoint);
        if (wallsInView) {
            if (leftWall.lineSegmentCrosses(lastLeftX, lastLeftY + lastVerticalChange, leftXStable, leftYStable + verticalChange)
                    || rightWall.lineSegmentCrosses(lastLeftX, lastLeftY + lastVerticalChange, leftXStable, leftYStable + verticalChange)
                    || centerDivider.lineSegmentCrosses(lastLeftX, lastLeftY + lastVerticalChange, leftXStable, leftYStable + verticalChange)) {
                if (!scheduledLoss && !inSecondChanceMenu) {
                    handleLoss();
                    return;
                }
            }
        }

        for (Section section : sectionsInView) {
            if (section.handleTouchMove(lastLeftX, leftXStable, lastLeftY + lastVerticalChange, leftYStable + verticalChange, false)) {
                if (!scheduledLoss && !inSecondChanceMenu) {
                    handleLoss();
                    return;
                }
            }
        }

        for (Section section : sectionsInView) {
            if(section.handleTouchMove(lastRightX, rightXStable, lastRightY + lastVerticalChange, rightYStable + verticalChange, true)) {
                if (!scheduledLoss && !inSecondChanceMenu) {
                    handleLoss();
                    return;
                }
            }
        }

        if (leftYStable + verticalChange <= sectionToPass) {
            leftPast = true;
        }


        boolean rightPast = false;
        Path.Point newRightPoint = new Path.Point();
        newRightPoint.x = rightXStable;
        newRightPoint.y = rightYStable + verticalChange;
        rightPath.addTopPoint(newRightPoint);
        if (wallsInView) {
            if (leftWall.lineSegmentCrosses(lastRightX, lastRightY + lastVerticalChange, rightXStable, rightYStable + verticalChange)
                    || rightWall.lineSegmentCrosses(lastRightX, lastRightY + lastVerticalChange, rightXStable, rightYStable + verticalChange)
                    || centerDivider.lineSegmentCrosses(lastRightX, lastRightY + lastVerticalChange, rightXStable, rightYStable + verticalChange)) {
                if (!scheduledLoss && !inSecondChanceMenu) {
                    handleLoss();
                    return;
                }
            }
        }
        if (rightYStable + verticalChange <= sectionToPass) {
            rightPast = true;
        }
        lastLeftX = leftXStable;
        lastLeftY = leftYStable;
        lastRightX = rightXStable;
        lastRightY = rightYStable;
        lastVerticalChange = verticalChange;
        if (leftPast && rightPast) {
            score += 1;
            refreshScore();
            if (currentSection != null) {
                currentSection = sectionsInView.get(sectionsInView.indexOf(currentSection) + 1);
            } else {
                currentSection = sectionsInView.get(0);
            }
            sectionToPass = nextSectionToPass;
            MediaPlayer mp = MediaPlayer.create(mainRenderer.getContext().getApplicationContext(), R.raw.sound_success);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    mp.release();
                }

            });
            mp.start();
        }


        int pointsToRemove = leftPath.points.size();
        float totalDistance = 0;
        if (leftPath.points.size() > 2) {
            int pointIndex = leftPath.getPoints().size() - 1;

            Path.Point point = leftPath.getPoints().get(pointIndex);
            float lastX = Float.NaN;
            float lastY = Float.NaN;
            float x = 0;
            float y = 0;
            float[] vertices = leftPath.getVertices();
            for (int j = vertices.length - 1; j >= 0; j--) {
                float vertice = vertices[j];
                if (j % 3 == 1) {
                    y = vertice;
                } else if (j % 3 == 0) {
                    x = vertice;
                    if (!Float.isNaN(lastX)) {
                        totalDistance += Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));
                        if (totalDistance >= height/4) {
                            leftPath.setAlphaVertice(j, (height/2 - totalDistance) / (height/4));
                        }
                    }
                    if (x == point.x && y == point.y) {
                        pointsToRemove--;
                        if (pointIndex != 0) {
                            pointIndex--;
                            point = leftPath.getPoints().get(pointIndex);
                        }
                    }
                    if (totalDistance >= height/2) {
                        break;
                    }
                    lastX = x;
                    lastY = y;
                }
            }
            leftPath.removeBottomPoints(pointsToRemove);
        }
        pointsToRemove = rightPath.getPoints().size();
        totalDistance = 0;
        if (rightPath.points.size() > 2) {

            int pointIndex = rightPath.getPoints().size() - 1;

            Path.Point point = rightPath.getPoints().get(pointIndex);
            float lastX = Float.NaN;
            float lastY = Float.NaN;
            float x = 0;
            float y = 0;
            float[] vertices = rightPath.getVertices();
            for (int j = vertices.length - 1; j >= 0; j--) {
                float vertice = vertices[j];
                if (j % 3 == 1) {
                    y = vertice;
                } else if (j % 3 == 0) {
                    x = vertice;
                    if (!Float.isNaN(lastX)) {
                        totalDistance += Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));
                        if (totalDistance >= height/4) {
                            rightPath.setAlphaVertice(j, (height/2 - totalDistance) / (height/4));
                        }
                    }
                    if (x == point.x && y == point.y) {
                        pointsToRemove--;
                        if (pointIndex != 0) {
                            pointIndex--;
                            point = rightPath.getPoints().get(pointIndex);
                        }
                    }
                    if (totalDistance >= height/2) {
                        break;
                    }
                    lastX = x;
                    lastY = y;
                }
            }
            rightPath.removeBottomPoints(pointsToRemove);
        }
    }

    private void calculateMove() {
        long time = System.currentTimeMillis();
        long dt = time - lastMoveCalc;
        if (dt < 0) {
            lastMoveCalc = time;
            return;
        }
        float newVerticalChange = (((float) dt) / 1000f) * speed;
        verticalChange -= newVerticalChange;
        if (dragControlScheme) {
            leftY = rightY = Math.min(height/2 - verticalChange + dragOffset, 17 * height / 24);
        }
        Matrix.translateM(verticalTranslate, 0, 0, newVerticalChange, 0);
        Iterator<Section> sectionIterator = sectionsInView.iterator();
        while (sectionIterator.hasNext()) {
            Section section = sectionIterator.next();
            if (section.getStartY() - section.getLength() > verticalChange + height) {
                sectionIterator.remove();
            }
        }
        if (wallsInView) {
            if (verticalChange < -height) {
                wallsInView = false;
                leftWall = null;
                rightWall = null;
                centerDivider = null;
            }
        }
        if (circlesInView) {
            if (verticalChange < -height/2 - height/10) {
                leftCircle = null;
                rightCircle = null;
                circlesInView = false;
            }
        }
        lastMoveCalc = time;
    }

    private void adjustSpeed() {
        long time = System.currentTimeMillis();
        long dt = time - lastSpeedCalculation;
        if (lastSpeedCalculation == -1 || dt < 0) {
            lastSpeedCalculation = time;
            return;
        }
        speed = 90 * ((float) Math.log(2 + score)) + 100;
        lastSpeedCalculation = time;
    }

    private Section getSection() {
        Section section = new DoubleSection();

        //Logistic function of score
        float difficulty = 1f/(1 + (float) Math.pow(Math.E, -(score - 20)/10f));
        section.setDifficulty(difficulty);

        return section;
    }

    private void generateSections() {
        if (endCurrentGenerate >= verticalChange - 10) {
            Section section = getSection();
            section.generate(random, 0, width, endCurrentGenerate);
            section.refresh();
            endCurrentGenerate = endCurrentGenerate - section.getLength();
            nextSectionToPass = endCurrentGenerate;
            sectionsInView.add(section);
        }
    }

    private void triggerRestart() {
        Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        tracker.setScreenName("Main Menu");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        score = 0;
        leftDown = false;
        rightDown = false;
        leftPath = new Path();
        leftPath.setWidth(10f);
        rightPath = new Path();
        rightPath.setWidth(10f);
        titleInView = true;
        circlesInView = true;
        wallsInView = true;
        currentSection = null;
        lastVerticalChange = 0;
        verticalChange = 0;
        titleAlpha = 1f;
        lastTitleCalculation = -1;
        sectionToPass = 0;
        nextSectionToPass = 0;
        endCurrentGenerate = 0;
        dragOffset = 0;
        currentRenderer = greyRenderType;
        backgroundRenderType = defaultBackgroundRenderer;
        Matrix.setIdentityM(verticalTranslate, 0);
        sectionsInView = new ArrayList<>();
        refreshDimensions(width, height, viewProjectionMatrix);
        if (!purchasedSecondChance) {
            gamesPlayedSinceAd++;
            if (initialAd && gamesPlayedSinceAd >= initialAds) {
                gamesPlayedSinceAd = gamesPerAd;
                initialAd = false;
            }
            if (gamesPlayedSinceAd >= gamesPerAd) {
                if (Math.random() > .5f) {
                    AdColonyVideoAd ad = new AdColonyVideoAd(MainActivity.interstitial_zone_id);
                    ad.show();
                } else {
                    try {
                        if (UnityAds.setZone("video") && UnityAds.canShow()) {
                            UnityAds.show();
                        }
                    } catch (IllegalStateException e) {
                        Log.e("Transverse", e.getMessage());
                    }
                }
                gamesPlayedSinceAd = 0;
            }
        }
        hadSecondChance = false;
        inSecondChanceMenu = false;
        scheduledRestart = false;
        scheduledLoss = false;
        inLossMenu = false;
        started = false;
    }


    @Override
    public void onDrawFrame() {
        if (refreshing) {
            return;
        }
        if (scheduledRestart) {
            triggerRestart();
        }
        if (scheduledEndGame) {
            finishGame();
        }
        if (scheduledSecondChance) {
            createSecondChance();
        }
        if (scheduledControlSwitch) {
            if (showIndicator && dragControlScheme) {
                showIndicator = true;
                dragControlScheme = false;
            } else if (showIndicator) {
                showIndicator = false;
            } else {
                dragControlScheme = true;
                showIndicator = true;
            }
            SharedPreferences pref = ((Activity) mainRenderer.getContext()).getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            edit.putBoolean("showIndicator", showIndicator);
            edit.putBoolean("dragControlScheme", dragControlScheme);
            edit.apply();
            refreshDimensions(width, height, viewProjectionMatrix);
            scheduledControlSwitch = false;
        }
        if (animatingColorChange) {
            long dt = System.currentTimeMillis() - timeOfChange;
            if (dt > 300) {
                animatingColorChange = false;
                dt = 300;
            }
            float red = ((300 - dt)/300f)*previousRenderType.getRed() + (dt/300f)*currentRenderer.getRed();
            float blue = ((300 - dt)/300f)*previousRenderType.getBlue() + (dt/300f)*currentRenderer.getBlue();
            float green = ((300 - dt)/300f)*previousRenderType.getGreen() + (dt/300f)*currentRenderer.getGreen();
            mixRenderType.setColor(red, green, blue);
            if (currentRenderer.isDualColor()) {
                float red2 = ((300 - dt)/300f)*previousRenderType.getRed2() + (dt/300f)*currentRenderer.getRed2();
                float blue2 = ((300 - dt)/300f)*previousRenderType.getBlue2() + (dt/300f)*currentRenderer.getBlue2();
                float green2 = ((300 - dt)/300f)*previousRenderType.getGreen2() + (dt/300f)*currentRenderer.getGreen2();
                mixRenderType.setDualColor(red2, green2, blue2, width);
            }
            red = ((300 - dt)/300f)*previousBackgroundRenderType.getRed() + (dt/300f)*backgroundRenderType.getRed();
            blue = ((300 - dt)/300f)*previousBackgroundRenderType.getBlue() + (dt/300f)*backgroundRenderType.getBlue();
            green = ((300 - dt)/300f)*previousBackgroundRenderType.getGreen() + (dt/300f)*backgroundRenderType.getGreen();
            mixBackgroundRenderType.setColor(red, green, blue);
            if (backgroundRenderType.isDualColor()) {
                float red2 = ((300 - dt)/300f)*previousBackgroundRenderType.getRed2() + (dt/300f)*backgroundRenderType.getRed2();
                float blue2 = ((300 - dt)/300f)*previousBackgroundRenderType.getBlue2() + (dt/300f)*backgroundRenderType.getBlue2();
                float green2 = ((300 - dt)/300f)*previousBackgroundRenderType.getGreen2() + (dt/300f)*backgroundRenderType.getGreen2();
                mixBackgroundRenderType.setDualColor(red2, green2, blue2, width);
            }

            mixBackgroundRenderType.setMatrix(viewProjectionMatrix);
            mixBackgroundRenderType.setAlpha(1);
            mixBackgroundRenderType.drawShape(backgroundRectangle);
        } else {
            backgroundRenderType.setMatrix(viewProjectionMatrix);
            backgroundRenderType.setAlpha(1);
            backgroundRenderType.drawShape(backgroundRectangle);
        }
        scoreRectangleRenderType.setMatrix(viewProjectionMatrix);
        scoreRectangleRenderType.setAlpha(1);
        titleRenderType.setMatrix(viewProjectionMatrix);
        if (!started) {
            greyRenderType.setMatrix(viewProjectionMatrix);
            lineRenderType.setMatrix(viewProjectionMatrix);
            fbRenderType.setMatrix(viewProjectionMatrix);
            fbRenderType.setAlpha(1);
            twitterRenderType.setMatrix(viewProjectionMatrix);
            twitterRenderType.setAlpha(1);
            titleRenderType.setAlpha(1);
            greyRenderType.drawShape(leftWall);
            greyRenderType.drawShape(centerDivider);
            greyRenderType.drawShape(rightWall);

            if (!dragControlScheme) {
                titleRenderType.drawShape(tapToStartRectangle);
                backgroundRenderType.drawText(tapAndHoldText);
                backgroundRenderType.drawText(toStartText);
            } else {
                titleRenderType.drawShape(leftDragRectangle);
                titleRenderType.drawShape(rightDragRectangle);
                titleRenderType.drawShape(dragInfoBox);
                backgroundRenderType.drawText(dragAndHoldOnBothSidesToStart);
                greyRenderType.drawShape(outerDragLeftCircle);
                greyRenderType.drawShape(outerDragRightCircle);
                backgroundRenderType.drawText(leftArrowsLeftText);
                backgroundRenderType.drawText(leftArrowsRightText);
                backgroundRenderType.drawText(rightArrowsLeftText);
                backgroundRenderType.drawText(rightArrowsRightText);
            }

            titleRenderType.drawShape(titleRectangle);
            backgroundRenderType.drawText(titleText);

            fbRenderType.drawShape(fbCircle);
            backgroundRenderType.drawImage(fbImage);

            twitterRenderType.drawShape(twitterCircle);
           backgroundRenderType.drawImage(twitterImage);

            titleRenderType.drawShape(titleHighScoreBox);
            backgroundRenderType.drawText(titleHighScoreText);

            titleRenderType.drawShape(leaderboardBox);
            backgroundRenderType.drawImage(leaderboardImage);

            titleRenderType.drawShape(achievementBox);
            backgroundRenderType.drawImage(achievementImage);

            titleRenderType.drawShape(switchControlSchemeBox);
            backgroundRenderType.drawText(switchControlsText);


            if (!purchasedSecondChance) {
                titleRenderType.drawShape(purchaseSecondChanceBox);
                backgroundRenderType.drawText(buyNoAdsAndText);
                backgroundRenderType.drawText(secondChancesText);
            }

            if (dragControlScheme) {
                if (leftDown) {
                    lineRenderType.drawShape(dragLeftCircle);
                } else {
                    backgroundRenderType.drawShape(dragLeftCircle);
                }
                if (rightDown) {
                    lineRenderType.drawShape(dragRightCircle);
                } else {
                    backgroundRenderType.drawShape(dragRightCircle);
                }
            } else {
                if (leftDown) {
                    lineRenderType.drawShape(leftCircle);
                } else {
                    greyRenderType.drawShape(leftCircle);
                }
                if (rightDown) {
                    lineRenderType.drawShape(rightCircle);
                } else {
                    greyRenderType.drawShape(rightCircle);
                }
            }

            if (!dragControlScheme && showIndicator) {
                lineRenderType.drawShape(dragLeftCircle);
                lineRenderType.drawShape(dragRightCircle);
            }
        } else if (inLossMenu || inSecondChanceMenu) {
            float[] verticalTranslateMVP = new float[16];
            Matrix.multiplyMM(verticalTranslateMVP, 0, viewProjectionMatrix, 0, verticalTranslate, 0);
            currentRenderer.setMatrix(verticalTranslateMVP);
            currentRenderer.setAlpha(1f);
            if (wallsInView) {
                currentRenderer.drawShape(leftWall);
                currentRenderer.drawShape(centerDivider);
                currentRenderer.drawShape(rightWall);
            }
            for (Section section : sectionsInView) {
                section.draw(verticalTranslateMVP, currentRenderer);
            }
            redRenderType.setMatrix(viewProjectionMatrix);
            redRenderType.setAlpha(1f);
            defaultBackgroundRenderer.setAlpha(1f);
            defaultBackgroundRenderer.setMatrix(viewProjectionMatrix);
            darkRedRenderType.setMatrix(viewProjectionMatrix);
            darkRedRenderType.setAlpha(1f);
            if (inLossMenu) {
                if (animatingLoss) {
                    long dt = System.currentTimeMillis() - timeOfLoss;
                    float alpha = dt*(1f/1000f);
                    if (alpha > 1) {
                        animatingLoss = false;
                    }
                    redRenderType.setAlpha(alpha);
                    redRenderType.drawShape(loseScoreRectangle);
                    redRenderType.drawShape(highScoreBox);
                    redRenderType.drawShape(retryRectangle);
                    redRenderType.drawShape(loseShareBox);
                    redRenderType.drawShape(loseAchievementBox);
                    redRenderType.drawShape(loseLeaderboardBox);
                    defaultBackgroundRenderer.drawText(loseScoreNumberText);
                    defaultBackgroundRenderer.drawText(loseScoreText);
                    defaultBackgroundRenderer.drawText(highScoreText);
                    defaultBackgroundRenderer.drawText(retryText);
                    defaultBackgroundRenderer.drawText(shareText);
                    defaultBackgroundRenderer.drawImage(loseAchievementImage);
                    defaultBackgroundRenderer.drawImage(loseLeaderboardImage);
                } else {
                    redRenderType.drawShape(loseScoreRectangle);
                    redRenderType.drawShape(highScoreBox);
                    redRenderType.drawShape(retryRectangle);
                    redRenderType.drawShape(loseShareBox);
                    redRenderType.drawShape(loseLeaderboardBox);
                    redRenderType.drawShape(loseAchievementBox);
                    defaultBackgroundRenderer.drawText(loseScoreNumberText);
                    defaultBackgroundRenderer.drawText(loseScoreText);
                    defaultBackgroundRenderer.drawText(highScoreText);
                    defaultBackgroundRenderer.drawText(retryText);
                    defaultBackgroundRenderer.drawText(shareText);
                    defaultBackgroundRenderer.drawImage(loseLeaderboardImage);
                    defaultBackgroundRenderer.drawImage(loseAchievementImage);
                }
            }

            if (inSecondChanceMenu) {
                redRenderType.drawShape(secondChanceBox);
                darkRedRenderType.drawShape(endGameButton);
                darkRedRenderType.drawShape(secondChanceButton);
                defaultBackgroundRenderer.drawText(gameText);
                defaultBackgroundRenderer.drawText(endText);
                defaultBackgroundRenderer.drawText(secondChanceText);
                if (!purchasedSecondChance) {
                    defaultBackgroundRenderer.drawText(watchVideoText);
                }
                greyRenderType.setMatrix(viewProjectionMatrix);
                defaultBackgroundRenderer.setMatrix(viewProjectionMatrix);
                if (score < 10) {
                    greyRenderType.drawText(scoreText);
                } else {
                    defaultBackgroundRenderer.drawText(scoreText);
                }
            }
        } else if (startingSecondChance) {
            float[] verticalTranslateMVP = new float[16];
            Matrix.multiplyMM(verticalTranslateMVP, 0, viewProjectionMatrix, 0, verticalTranslate, 0);
            greyRenderType.setAlpha(1);
            currentRenderer.setAlpha(1);
            titleRenderType.setAlpha(1);
            titleRenderType.setMatrix(verticalTranslateMVP);
            greyRenderType.setMatrix(verticalTranslateMVP);
            currentRenderer.setMatrix(verticalTranslateMVP);
            defaultBackgroundRenderer.setMatrix(verticalTranslateMVP);
            if (wallsInView) {
                currentRenderer.drawShape(leftWall);
                currentRenderer.drawShape(centerDivider);
                currentRenderer.drawShape(rightWall);
            }
            for (Section section : sectionsInView) {
                section.draw(verticalTranslateMVP, currentRenderer);
            }
            if (!dragControlScheme) {
                titleRenderType.drawShape(startingSecondChanceRectangle);
                defaultBackgroundRenderer.drawText(secondChanceTapAndHoldText);
                defaultBackgroundRenderer.drawText(toRetryText);
                if (leftDown) {
                    lineRenderType.drawShape(leftSecondChanceCircle);
                } else {
                    greyRenderType.drawShape(leftSecondChanceCircle);
                }
                if (rightDown) {
                    lineRenderType.drawShape(rightSecondChanceCircle);
                } else {
                    greyRenderType.drawShape(rightSecondChanceCircle);
                }
            } else {
                titleRenderType.drawShape(leftDragRectangle);
                titleRenderType.drawShape(rightDragRectangle);
                titleRenderType.drawShape(dragInfoBox);
                defaultBackgroundRenderer.drawText(dragAndHoldOnBothSidesToStart);
                greyRenderType.drawShape(outerDragLeftCircle);
                greyRenderType.drawShape(outerDragRightCircle);
                defaultBackgroundRenderer.drawText(leftArrowsLeftText);
                defaultBackgroundRenderer.drawText(leftArrowsRightText);
                defaultBackgroundRenderer.drawText(rightArrowsLeftText);
                defaultBackgroundRenderer.drawText(rightArrowsRightText);
                if (leftDown) {
                    lineRenderType.drawShape(dragLeftCircle);
                } else {
                    defaultBackgroundRenderer.drawShape(dragLeftCircle);
                }
                if (rightDown) {
                    lineRenderType.drawShape(dragRightCircle);
                } else {
                    defaultBackgroundRenderer.drawShape(dragRightCircle);
                }
            }


            if (!dragControlScheme && showIndicator) {
                lineRenderType.drawShape(dragLeftCircle);
                lineRenderType.drawShape(dragRightCircle);
            }

            greyRenderType.setMatrix(viewProjectionMatrix);
            defaultBackgroundRenderer.setMatrix(viewProjectionMatrix);
            if (score < 10) {
                greyRenderType.drawText(scoreText);
            } else {
                defaultBackgroundRenderer.drawText(scoreText);
            }
        } else {
            if (scheduledLoss) {
                handleLoss();
            }
            calculateMove();
            adjustSpeed();
            generateSections();
            handleSectionTouch();
            float[] verticalTranslateMVP = new float[16];
            Matrix.multiplyMM(verticalTranslateMVP, 0, viewProjectionMatrix, 0, verticalTranslate, 0);
            for (Section section : sectionsInView) {
                if (animatingColorChange) {
                    section.draw(verticalTranslateMVP, mixRenderType);
                } else {
                    section.draw(verticalTranslateMVP, currentRenderer);
                }
            }
            lineRenderType.setMatrix(verticalTranslateMVP);
            if (wallsInView) {
                if (animatingColorChange) {
                    mixRenderType.setMatrix(verticalTranslateMVP);
                    mixRenderType.drawShape(leftWall);
                    mixRenderType.drawShape(centerDivider);
                    mixRenderType.drawShape(rightWall);
                } else {
                    currentRenderer.setMatrix(verticalTranslateMVP);
                    currentRenderer.drawShape(leftWall);
                    currentRenderer.drawShape(centerDivider);
                    currentRenderer.drawShape(rightWall);
                }
            }
            if (titleInView) {
                long time = System.currentTimeMillis();
                long dt = time - lastTitleCalculation;
                boolean sane = true;
                if (lastTitleCalculation == -1 || dt <= 0) {
                    lastTitleCalculation = time;
                    sane = false;
                    defaultBackgroundRenderer.setAlpha(titleAlpha);
                    titleRenderType.setAlpha(titleAlpha);
                    fbRenderType.setAlpha(titleAlpha);
                    twitterRenderType.setAlpha(titleAlpha);
                    titleRenderType.drawShape(titleRectangle);
                    defaultBackgroundRenderer.drawText(titleText);
                    fbRenderType.drawShape(fbCircle);
                    defaultBackgroundRenderer.drawImage(fbImage);
                    twitterRenderType.drawShape(twitterCircle);
                    defaultBackgroundRenderer.drawImage(twitterImage);
                    if (!dragControlScheme) {
                        titleRenderType.drawShape(tapToStartRectangle);
                        defaultBackgroundRenderer.drawText(tapAndHoldText);
                        defaultBackgroundRenderer.drawText(toStartText);
                    } else {
                        titleRenderType.drawShape(leftDragRectangle);
                        titleRenderType.drawShape(rightDragRectangle);
                        titleRenderType.drawShape(dragInfoBox);
                        defaultBackgroundRenderer.drawText(dragAndHoldOnBothSidesToStart);
                        greyRenderType.drawShape(outerDragLeftCircle);
                        greyRenderType.drawShape(outerDragRightCircle);
                        defaultBackgroundRenderer.drawText(leftArrowsLeftText);
                        defaultBackgroundRenderer.drawText(leftArrowsRightText);
                        defaultBackgroundRenderer.drawText(rightArrowsLeftText);
                        defaultBackgroundRenderer.drawText(rightArrowsRightText);
                    }

                    titleRenderType.drawShape(titleHighScoreBox);
                    defaultBackgroundRenderer.drawText(titleHighScoreText);
                    titleRenderType.drawShape(leaderboardBox);
                    titleRenderType.drawShape(achievementBox);
                    titleRenderType.drawShape(switchControlSchemeBox);
                    defaultBackgroundRenderer.drawText(switchControlsText);
                    defaultBackgroundRenderer.drawImage(achievementImage);
                    defaultBackgroundRenderer.drawImage(leaderboardImage);

                    if (!purchasedSecondChance) {
                        titleRenderType.drawShape(purchaseSecondChanceBox);
                        defaultBackgroundRenderer.drawText(buyNoAdsAndText);
                        defaultBackgroundRenderer.drawText(secondChancesText);
                    }
                }
                if (sane) {
                    float alphaChange = dt * (1/500f);
                    titleAlpha -= alphaChange;
                    if (titleAlpha < 0) {
                       titleInView = false;
                       titleAlpha = 0;
                    }
                    lastTitleCalculation = time;
                    titleRenderType.setAlpha(titleAlpha);
                    defaultBackgroundRenderer.setAlpha(titleAlpha);
                    fbRenderType.setAlpha(titleAlpha);
                    twitterRenderType.setAlpha(titleAlpha);

                    titleRenderType.drawShape(titleRectangle);
                    fbRenderType.drawShape(fbCircle);
                    twitterRenderType.drawShape(twitterCircle);
                    titleRenderType.drawShape(titleHighScoreBox);
                    titleRenderType.drawShape(leaderboardBox);
                    titleRenderType.drawShape(achievementBox);
                    titleRenderType.drawShape(switchControlSchemeBox);

                    if (!dragControlScheme) {
                        titleRenderType.drawShape(tapToStartRectangle);
                        defaultBackgroundRenderer.drawText(tapAndHoldText);
                        defaultBackgroundRenderer.drawText(toStartText);
                    } else {
                        titleRenderType.drawShape(leftDragRectangle);
                        titleRenderType.drawShape(rightDragRectangle);
                        titleRenderType.drawShape(dragInfoBox);
                        defaultBackgroundRenderer.drawText(dragAndHoldOnBothSidesToStart);
                        greyRenderType.drawShape(outerDragLeftCircle);
                        greyRenderType.drawShape(outerDragRightCircle);
                        defaultBackgroundRenderer.drawText(leftArrowsLeftText);
                        defaultBackgroundRenderer.drawText(leftArrowsRightText);
                        defaultBackgroundRenderer.drawText(rightArrowsLeftText);
                        defaultBackgroundRenderer.drawText(rightArrowsRightText);
                    }

                    defaultBackgroundRenderer.drawText(titleText);
                    defaultBackgroundRenderer.drawText(titleHighScoreText);
                    defaultBackgroundRenderer.drawImage(achievementImage);
                    defaultBackgroundRenderer.drawImage(leaderboardImage);
                    defaultBackgroundRenderer.drawImage(fbImage);
                    defaultBackgroundRenderer.drawImage(twitterImage);
                    defaultBackgroundRenderer.drawText(switchControlsText);
                    if (!purchasedSecondChance) {
                        titleRenderType.drawShape(purchaseSecondChanceBox);
                        defaultBackgroundRenderer.drawText(buyNoAdsAndText);
                        defaultBackgroundRenderer.drawText(secondChancesText);
                    }

                    defaultBackgroundRenderer.setAlpha(1f);
                    if (!titleInView) {
                        titleRectangle = null;
                        titleText = null;
                        tapToStartRectangle = null;
                        tapAndHoldText = null;
                        toStartText = null;
                    }
                }
            }
            if (circlesInView && !dragControlScheme) {
                lineRenderType.drawShape(leftCircle);
                lineRenderType.drawShape(rightCircle);
            }

            if (showIndicator) {
                float indicatorY;
                if (dragControlScheme) {
                    indicatorY = Math.min(height/2 - verticalChange + dragOffset, 17*height/24);
                } else {
                    indicatorY = height/2;
                }
                dragLeftCircle.setCenter(leftX, indicatorY, 0);
                dragLeftCircle.refresh();
                dragRightCircle.setCenter(rightX, indicatorY, 0);
                dragRightCircle.refresh();
                lineRenderType.setMatrix(viewProjectionMatrix);
                lineRenderType.drawShape(dragLeftCircle);
                lineRenderType.drawShape(dragRightCircle);
            }

            lineRenderType.setMatrix(verticalTranslateMVP);
            lineRenderType.drawPath(leftPath);
            lineRenderType.drawPath(rightPath);
            greyRenderType.setMatrix(viewProjectionMatrix);
            defaultBackgroundRenderer.setMatrix(viewProjectionMatrix);
            if (score < 10) {
                greyRenderType.drawText(scoreText);
            } else {
                defaultBackgroundRenderer.drawText(scoreText);
            }
        }
    }

    @Override
    public void refreshDimensions(float width, float height, float[] viewProjectionMatrix) {
        refreshing = true;
        if (circlesInView && !dragControlScheme) {
            leftCircle = new Circle();
            leftCircle.setPrecision(360);
            leftCircle.setRadius(height/10);
            rightCircle = new Circle();
            rightCircle.setPrecision(360);
            rightCircle.setRadius(height/10);
            leftCircle.setCenter(width/4, height/2, 0);
            rightCircle.setCenter(3*width/4, height/2, 0);
            leftCircle.refresh();
            rightCircle.refresh();
        }
        if (wallsInView) {
            leftWall = new Rectangle();
            leftWall.setOrigin(0, 0, 0);
            leftWall.setWidth(15f);
            leftWall.setHeight(height);
            leftWall.refresh();
            centerDivider = new Rectangle();
            centerDivider.setOrigin(width/2 - 7.5f, 0, 0);
            centerDivider.setWidth(15f);
            centerDivider.setHeight(height);
            centerDivider.refresh();
            rightWall = new Rectangle();
            rightWall.setOrigin(width - 15, 0, 0);
            rightWall.setWidth(15f);
            rightWall.setHeight(height);
            rightWall.refresh();
        }
        if (titleInView) {
            titleRectangle = new RoundedRectangle();
            float titleRectangleWidth = 2*width/3;
            titleRectangle.setWidth(titleRectangleWidth);
            float titleRectangleY = height/5;
            float titleRectangleHeight = height/5;
            titleRectangle.setCenter(width/2, titleRectangleY, 0);
            titleRectangle.setHeight(titleRectangleHeight);
            titleRectangle.setCornerRadius(10f);
            titleRectangle.setPrecision(60);
            titleRectangle.refresh();
            titleText = new Text();
            titleText.setFont("FFF Forward");
            titleText.setText("Transverse");
            titleText.setTextSize(titleRectangleHeight - 10);
            titleText.setOrigin(width/2 - titleText.getWidth()/2, titleRectangleY - (titleRectangleHeight - 10)/2, 0);
            titleText.refresh();

            fbCircle = new Circle();
            fbCircle.setCenter(width/2 + width/3 + width/20, height/5, 0);
            fbCircle.setRadius(width/30);
            fbCircle.setPrecision(360);
            fbCircle.refresh();

            float fbImageWidth = width/25;
            float fbImageHeight = width/25;
            fbImage = new Image();
            fbImage.setTextureHandle(Textures.fbTexture);
            fbImage.setVertices(new float[] {
                    width/2 + width/3 + width/20 - fbImageWidth/2, height/5 - fbImageHeight/2, 0,
                    width/2 + width/3 + width/20 - fbImageWidth/2, height/5 + fbImageHeight/2, 0,
                    width/2 + width/3 + width/20 + fbImageWidth/2, height/5 + fbImageHeight/2, 0,
                    width/2 + width/3 + width/20 + fbImageWidth/2, height/5 - fbImageHeight/2, 0
            });
            fbImage.setDrawOrder(new short[] {
                    0,1,2,0,2,3
            });
            fbImage.setUVCoordinates(new float[] {
                    0,0,
                    0,1,
                    1,1,
                    1,0
            });
            fbImage.refresh();

            twitterCircle = new Circle();
            twitterCircle.setCenter(width/2 - width/3 - width/20, height/5, 0);
            twitterCircle.setRadius(width/30);
            twitterCircle.setPrecision(360);
            twitterCircle.refresh();

            float twitterImageWidth = width/20;
            float twitterImageHeight = width/20;
            twitterImage = new Image();
            twitterImage.setTextureHandle(Textures.twitterTexture);
            twitterImage.setVertices(new float[] {
                    width/2 - width/3 - width/20 - twitterImageWidth/2, height/5 - twitterImageHeight/2, 0,
                    width/2 - width/3 - width/20 - twitterImageWidth/2, height/5 + twitterImageHeight/2, 0,
                    width/2 - width/3 - width/20 + twitterImageWidth/2, height/5 + twitterImageHeight/2, 0,
                    width/2 - width/3 - width/20 + twitterImageWidth/2, height/5 - twitterImageHeight/2, 0
            });
            twitterImage.setDrawOrder(new short[] {
                    0,1,2,0,2,3
            });
            twitterImage.setUVCoordinates(new float[] {
                    0,0,
                    0,1,
                    1,1,
                    1,0
            });
            twitterImage.refresh();

            dragLeftCircle = new Circle();
            dragLeftCircle.setPrecision(60);
            dragLeftCircle.setRadius(height/60);
            dragRightCircle = new Circle();
            dragRightCircle.setPrecision(60);
            dragRightCircle.setRadius(height/60);
            dragLeftCircle.setCenter(width/4, height/2, 0);
            dragRightCircle.setCenter(3*width/4, height/2, 0);
            dragLeftCircle.refresh();
            dragRightCircle.refresh();
            if (!dragControlScheme) {
                tapToStartRectangle = new RoundedRectangle();
                tapToStartRectangle.setWidth(titleRectangleWidth);
                tapToStartRectangle.setCenter(width / 2, height / 2, 0);
                tapToStartRectangle.setHeight(height / 4);
                tapToStartRectangle.setCornerRadius(10f);
                tapToStartRectangle.setPrecision(60);
                tapToStartRectangle.refresh();
                tapAndHoldText = new Text();
                tapAndHoldText.setFont("FFF Forward");
                tapAndHoldText.setText("Tap and hold");
                tapAndHoldText.setTextSize((height / 4) / 3);
                tapAndHoldText.setOrigin(width / 2 - tapAndHoldText.getWidth() / 2, height / 2 - (height / 4) / 3, 0);
                tapAndHoldText.refresh();
                toStartText = new Text();
                toStartText.setFont("FFF Forward");
                toStartText.setText("to start!");
                toStartText.setTextSize((height / 4) / 3);
                toStartText.setOrigin(width / 2 - toStartText.getWidth() / 2, height / 2, 0);
                toStartText.refresh();
            } else {
                outerDragLeftCircle = new Circle();
                outerDragLeftCircle.setPrecision(360);
                outerDragLeftCircle.setRadius(height/15);
                outerDragRightCircle = new Circle();
                outerDragRightCircle.setPrecision(360);
                outerDragRightCircle.setRadius(height/15);
                outerDragLeftCircle.setCenter(width/4, height/2, 0);
                outerDragRightCircle.setCenter(3*width/4, height/2, 0);
                outerDragLeftCircle.refresh();
                outerDragRightCircle.refresh();
                rightDragRectangle = new Rectangle();
                rightDragRectangle.setOrigin(width/2 + 7.5f, height/2, 0);
                rightDragRectangle.setHeight(height / 8);
                rightDragRectangle.setWidth(width/2 - 22.5f);
                rightDragRectangle.refresh();
                leftDragRectangle = new Rectangle();
                leftDragRectangle.setOrigin(15f, height/2, 0);
                leftDragRectangle.setHeight(height / 8);
                leftDragRectangle.setWidth(width/2 - 22.5f);
                leftDragRectangle.refresh();

                leftArrowsLeftText = new Text();
                leftArrowsLeftText.setText("<<<");
                leftArrowsLeftText.setFont("FFF Forward");
                leftArrowsLeftText.setTextSize(height/12);
                leftArrowsLeftText.setOrigin(15 + (width - 45)/8 - leftArrowsLeftText.getWidth()/2, 9 * height/ 16 - height/24, 0);
                leftArrowsLeftText.refresh();

                leftArrowsRightText = new Text();
                leftArrowsRightText.setText(">>>");
                leftArrowsRightText.setFont("FFF Forward");
                leftArrowsRightText.setTextSize(height/12);
                leftArrowsRightText.setOrigin(15 + 3*(width - 45)/8 - leftArrowsRightText.getWidth()/2, 9 * height/ 16 - height/24, 0);
                leftArrowsRightText.refresh();

                rightArrowsLeftText = new Text();
                rightArrowsLeftText.setText("<<<");
                rightArrowsLeftText.setFont("FFF Forward");
                rightArrowsLeftText.setTextSize(height/12);
                rightArrowsLeftText.setOrigin(30 + 5*(width - 45)/8 - rightArrowsLeftText.getWidth()/2, 9 * height/ 16 - height/24, 0);
                rightArrowsLeftText.refresh();

                rightArrowsRightText = new Text();
                rightArrowsRightText.setText(">>>");
                rightArrowsRightText.setFont("FFF Forward");
                rightArrowsRightText.setTextSize(height/12);
                rightArrowsRightText.setOrigin(30 + 7*(width - 45)/8 - rightArrowsRightText.getWidth()/2, 9 * height/16 - height/24, 0);
                rightArrowsRightText.refresh();

                dragInfoBox = new Rectangle();
                dragInfoBox.setOrigin(15, height/2 - height/12 - height/11, 0);
                dragInfoBox.setWidth(width - 30);
                dragInfoBox.setHeight(height/12);
                dragInfoBox.refresh();

                dragAndHoldOnBothSidesToStart = new Text();
                dragAndHoldOnBothSidesToStart.setText("Drag and hold on both sides to start!");
                dragAndHoldOnBothSidesToStart.setFont("FFF Forward");
                dragAndHoldOnBothSidesToStart.setTextSize(height/16);
                dragAndHoldOnBothSidesToStart.setOrigin(width/2 - dragAndHoldOnBothSidesToStart.getWidth()/2, height/2 - height/11 - height/24 - height/32, 0);
                dragAndHoldOnBothSidesToStart.refresh();
            }

            float bottomButtonHeight = height/6;

            titleHighScoreBox = new RoundedRectangle();
            titleHighScoreBox.setHeight(bottomButtonHeight);
            titleHighScoreBox.setCenter(width/2, 3*height/4, 0);
            titleHighScoreBox.setCornerRadius(10f);
            titleHighScoreBox.setWidth(11*width/32);
            titleHighScoreBox.setPrecision(60);
            titleHighScoreBox.refresh();

            titleHighScoreText = new Text();
            titleHighScoreText.setFont("FFF Forward");
            titleHighScoreText.setText("Best: " + highScore);
            titleHighScoreText.setTextSize((2*height/3)/5);
            titleHighScoreText.setOrigin(width/2 - titleHighScoreText.getWidth()/2, 3*height/4 - (2*height/3)/10, 0);
            titleHighScoreText.refresh();

            switchControlSchemeBox = new RoundedRectangle();
            switchControlSchemeBox.setHeight(height/10);
            switchControlSchemeBox.setCenter(width/2, 11 * height / 12
                    , 0);
            switchControlSchemeBox.setCornerRadius(10f);
            switchControlSchemeBox.setWidth(11 * width / 32);
            switchControlSchemeBox.setPrecision(60);
            switchControlSchemeBox.refresh();

            switchControlsText = new Text();
            switchControlsText.setFont("FFF Forward");
            switchControlsText.setText("Switch Controls");
            switchControlsText.setTextSize(height/16);
            switchControlsText.setOrigin(width/2 - switchControlsText.getWidth()/2, 11*height/12 - height/32, 0);
            switchControlsText.refresh();

            float leaderboardCenterX = width/2 - 11 * width / 64 - width / 36 - bottomButtonHeight/2;

            leaderboardBox = new RoundedRectangle();
            leaderboardBox.setHeight(bottomButtonHeight);
            leaderboardBox.setWidth(bottomButtonHeight);
            leaderboardBox.setCornerRadius(10f);
            leaderboardBox.setPrecision(60);
            leaderboardBox.setCenter(leaderboardCenterX, 4*height/5, 0);
            leaderboardBox.refresh();

            float leaderboardImageHeight = 5*bottomButtonHeight/8;
            float leaderboardImageWidth = leaderboardImageHeight * (196f/210f);

            leaderboardImage = new Image();
            leaderboardImage.setTextureHandle(Textures.leaderboardTexture);
            leaderboardImage.setVertices(new float[] {
                    leaderboardCenterX - leaderboardImageWidth/2, 4*height/5 - leaderboardImageHeight/2, 0,
                    leaderboardCenterX - leaderboardImageWidth/2, 4*height/5 + leaderboardImageHeight/2, 0,
                    leaderboardCenterX + leaderboardImageWidth/2, 4*height/5 + leaderboardImageHeight/2, 0,
                    leaderboardCenterX + leaderboardImageWidth/2, 4*height/5 - leaderboardImageHeight/2, 0
            });
            leaderboardImage.setDrawOrder(new short[] {
                    0,1,2,0,2,3
            });
            leaderboardImage.setUVCoordinates(new float[] {
                    0,0,
                    0,1,
                    1,1,
                    1,0
            });
            leaderboardImage.refresh();

            float achievementCenterX;

            if (!purchasedSecondChance) {
                achievementCenterX = width/2 - 11 * width / 64 - width / 36 - bottomButtonHeight - width/36 - bottomButtonHeight/2;
                purchaseSecondChanceBox = new RoundedRectangle();
                purchaseSecondChanceBox.setHeight(bottomButtonHeight);
                purchaseSecondChanceBox.setWidth(width/4);
                purchaseSecondChanceBox.setCenter(width/2 + 11 * width/64 + width/36 + width/8, 4*height/5, 0);
                purchaseSecondChanceBox.setPrecision(60);
                purchaseSecondChanceBox.setCornerRadius(10f);
                purchaseSecondChanceBox.refresh();

                buyNoAdsAndText = new Text();
                buyNoAdsAndText.setFont("FFF Forward");
                buyNoAdsAndText.setText("No Ads And");
                buyNoAdsAndText.setTextSize(height/16);
                buyNoAdsAndText.setOrigin(width/2 + 11 * width/64 + width/36 + width/8 - buyNoAdsAndText.getWidth()/2, 4*height/5 - height/16, 0);
                buyNoAdsAndText.refresh();

                secondChancesText = new Text();
                secondChancesText.setFont("FFF Forward");
                secondChancesText.setText("Second Chances");
                secondChancesText.setTextSize(height/16);
                secondChancesText.setOrigin(width/2 + 11 * width/64 + width/36 + width/8 - secondChancesText.getWidth()/2, 4*height/5, 0);
                secondChancesText.refresh();
            } else {
                achievementCenterX = width/2 + 11 * width / 64 + width / 36 + bottomButtonHeight/2;
            }
            achievementBox = new RoundedRectangle();
            achievementBox.setHeight(bottomButtonHeight);
            achievementBox.setWidth(bottomButtonHeight);
            achievementBox.setCornerRadius(10f);
            achievementBox.setPrecision(60);
            achievementBox.setCenter(achievementCenterX, 4*height/5, 0);
            achievementBox.refresh();

            float achievementImageWidth = 5*bottomButtonHeight/8;
            float achievementImageHeight = achievementImageWidth * (215f/256f);
            achievementImage = new Image();
            achievementImage.setTextureHandle(Textures.trophyTexture);
            achievementImage.setVertices(new float[] {
                    achievementCenterX - achievementImageWidth/2, 4*height/5 - achievementImageHeight/2, 0,
                    achievementCenterX - achievementImageWidth/2, 4*height/5 + achievementImageHeight/2, 0,
                    achievementCenterX + achievementImageWidth/2, 4*height/5 + achievementImageHeight/2, 0,
                    achievementCenterX + achievementImageWidth/2, 4*height/5 - achievementImageHeight/2, 0
            });
            achievementImage.setDrawOrder(new short[] {
                    0,1,2,0,2,3
            });
            achievementImage.setUVCoordinates(new float[] {
                    0,0,
                    0,1,
                    1,1,
                    1,0
            });
            achievementImage.refresh();
        }
        this.viewProjectionMatrix = viewProjectionMatrix;
        this.height = height;
        this.width = width;
        backgroundRectangle = new Rectangle();
        backgroundRectangle.setWidth(width);
        backgroundRectangle.setHeight(height);
        backgroundRectangle.setOrigin(0, 0, 0);
        backgroundRectangle.refresh();
        scoreText = new Text();
        scoreText.setFont("FFF Forward");
        scoreText.setTextSize(50f);
        scoreText.setText(score + "");
        scoreText.setOrigin(width - 25 - scoreText.getWidth(), 3, 0);
        scoreText.refresh();

        refreshing = false;
    }

    private void refreshScore() {

        scoreText.setText(score + "");
        scoreText.setOrigin(width - 25 - scoreText.getWidth(), 3, 0);
        scoreText.refresh();
        if (score != 0) {
            float brightness;
            float bgBrightness;
            float saturation;
            float bgSaturation;
            if (score < 5) {
                if (random.nextFloat() < .15f) {
                    saturation = .5f*random.nextFloat();
                    brightness = .3f*random.nextFloat();
                } else {
                    saturation = 1;
                    brightness = .55f + .3f*random.nextFloat();
                }
                bgSaturation = .1f;
                bgBrightness = .95f;
            } else if (score < 10) {
                if (random.nextFloat() < .15f) {
                    saturation = .5f*random.nextFloat();
                    brightness = .3f*random.nextFloat();
                } else {
                    saturation = 1;
                    brightness = .3f + .3f*random.nextFloat();
                }
                bgSaturation = .3f;
                bgBrightness = .90f;
            } else if (score < 15) {
                bgSaturation = 1;
                bgBrightness = .55f + .3f*random.nextFloat();
                saturation = .1f;
                brightness = .95f;
            } else if (score < 20) {
                bgSaturation = 1;
                bgBrightness = .55f + .3f*random.nextFloat();
                saturation = 0f;
                brightness = 0f;
            } else if (score < 30) {
                saturation = 1;
                brightness = 1;
                bgSaturation = 0f;
                bgBrightness = 0f;
            } else if (score < 40) {
                saturation = 1;
                brightness = 1;
                bgSaturation = 0f;
                bgBrightness = 0f;
            } else {
                saturation = 0;
                brightness = 0;
                bgSaturation = .7f;
                bgBrightness = 1f;
            }


            float hue = random.nextFloat()*360;

            SolidRenderType renderType = new SolidRenderType();
            int renderColor = Color.HSVToColor(new float[] {hue, saturation, brightness});
            renderType.setColor(Color.red(renderColor)/255f, Color.green(renderColor)/255f, Color.blue(renderColor)/255f);
            renderType.setAlpha(1);
            previousRenderType = currentRenderer;
            currentRenderer = renderType;

            SolidRenderType backgroundRenderType = new SolidRenderType();
            int backgroundRenderColor = Color.HSVToColor(new float[] {hue, bgSaturation, bgBrightness});
            backgroundRenderType.setColor(Color.red(backgroundRenderColor)/255f, Color.green(backgroundRenderColor)/255f, Color.blue(backgroundRenderColor)/255f);
            backgroundRenderType.setAlpha(1);

            previousBackgroundRenderType = this.backgroundRenderType;
            this.backgroundRenderType = backgroundRenderType;

            mixRenderType = new SolidRenderType();
            mixRenderType.setAlpha(1);
            mixRenderType.setColor(previousRenderType.getRed(), previousRenderType.getGreen(), previousRenderType.getBlue());

            mixBackgroundRenderType = new SolidRenderType();
            mixBackgroundRenderType.setAlpha(1);
            mixBackgroundRenderType.setColor(previousBackgroundRenderType.getRed()/255f, previousBackgroundRenderType.getGreen()/255f, previousBackgroundRenderType.getBlue()/255f);

            if (score >= 30 && score < 40) {
                float hue2;
                do {
                    hue2 = random.nextFloat() * 360;
                } while (Math.min(((hue2 - hue) + 360) % 360, (-(hue2 - hue) + 360) % 360) < 130);
                int renderColor2 = Color.HSVToColor(new float[] {hue2, saturation, brightness});
                renderType.setDualColor(Color.red(renderColor2)/255f, Color.green(renderColor2)/255f, Color.blue(renderColor2)/255f, width);
                if (!previousRenderType.isDualColor()) {
                    previousRenderType.setDualColor(previousRenderType.getRed(), previousRenderType.getGreen(), previousRenderType.getBlue(), width);
                }
                mixRenderType.setDualColor(previousRenderType.getRed2(), previousRenderType.getGreen2(), previousRenderType.getBlue2(), width);
            } else if (score >= 40) {
                float hue2;
                do {
                    hue2 = random.nextFloat() * 360;
                } while (Math.min(((hue2 - hue) + 360) % 360, (-(hue2 - hue) + 360) % 360) < 130);
                int renderColor2 = Color.HSVToColor(new float[] {hue2, bgSaturation, bgBrightness});
                backgroundRenderType.setDualColor(Color.red(renderColor2)/255f, Color.green(renderColor2)/255f, Color.blue(renderColor2)/255f, width);
                if (!previousBackgroundRenderType.isDualColor()) {
                    previousBackgroundRenderType.setDualColor(previousBackgroundRenderType.getRed(), previousBackgroundRenderType.getGreen(), previousBackgroundRenderType.getBlue(), width);
                }
                mixBackgroundRenderType.setDualColor(previousBackgroundRenderType.getRed2(), previousBackgroundRenderType.getGreen2(), previousBackgroundRenderType.getBlue2(), width);
            }

            animatingColorChange = true;
            timeOfChange = System.currentTimeMillis();
        }
    }

    @Override
    public void onAdColonyV4VCReward(AdColonyV4VCReward adColonyV4VCReward) {
        if (adColonyV4VCReward.success() && inSecondChanceMenu) {
            scheduledSecondChance = true;
        }
    }

    @Override
    public void onHide() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoCompleted(String itemID, boolean skipped) {
        if (!skipped && inSecondChanceMenu && UnityAds.getZone().equals("rewardedVideo")) {
            scheduledSecondChance = true;
        }
    }

    @Override
    public void onFetchCompleted() {

    }

    @Override
    public void onFetchFailed() {

    }

    public void setNoAds() {
        purchasedSecondChance = true;
        refreshDimensions(width, height, viewProjectionMatrix);
    }

    public void onResume() {
        if (inLossMenu) {
            Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            tracker.setScreenName("Loss Menu");
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } else if (!started) {
            Tracker tracker = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
            tracker.setScreenName("Main Menu");
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }
}
