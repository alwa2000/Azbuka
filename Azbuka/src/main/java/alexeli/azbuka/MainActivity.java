package alexeli.azbuka;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import alexeli.azbuka.logger.ILogger;
import alexeli.azbuka.logger.LoggerFactory;
import alexeli.azbuka.xml.XMLParser;

public class MainActivity extends Activity implements View.OnClickListener {

    private static ILogger mLog = LoggerFactory.create();
    private static final String TAG = "MainActivity";

    private ImageView imageView;
    private TextView textViewDecsription;
    private TextView textViewLetter;

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundsMap;

    private ArrayList<Card> cards;

    private int index = 0;
    private int max_index = 2;
    private int swipeMinDistance;
    private int swipeThresholdVelocity;
    private int swipeMaxOffPath;


    // All static variables
    static final String XMLFilename = "rus_letters.xml";
    // XML node keys
    static final String KEY_CARD = "card"; // parent node
    static final String KEY_POSITION = "position";
    static final String KEY_LETTER = "letter";
    static final String KEY_SND_LETTER = "snd_letter";
    static final String KEY_IMG = "img";
    static final String KEY_SND_IMG = "snd_img";
    static final String KEY_DESC = "desc";

    // (there is also vc.getScaledMaximumFlingVelocity() one could check against)
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);


//        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 100);
//        soundsMap = new HashMap<Integer, Integer>();
//        soundsMap.put(0, soundPool.load(this, R.raw.rus_a, 1));
//        soundsMap.put(1, soundPool.load(this, R.raw.rus_b, 1));
//        soundsMap.put(2, soundPool.load(this, R.raw.rus_g, 1));
//        soundsMap.put(3, soundPool.load(this, R.raw.arbuz, 1));
//        soundsMap.put(4, soundPool.load(this, R.raw.begemot, 1));
//        soundsMap.put(5, soundPool.load(this, R.raw.gepard, 1));

//        playLetter();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                playDescription();
//            }
//        },800);

//        playDescription();


        imageView = (ImageView) findViewById(R.id.imageView);
        textViewDecsription = (TextView) findViewById(R.id.textView);
        textViewLetter = (TextView) findViewById(R.id.textView2);

        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playDescription();
            }
        });

        textViewDecsription.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playDescription();
            }
        });

        textViewLetter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playLetter();
            }
        });

//        imageView.setImageResource(R.drawable.arbuz);
//        setDescription(R.string.desc_rus1);
//        //textViewDecsription.setText(R.string.desc_rus1);
//        textViewLetter.setText(R.string.letter_rus1);

        final ViewConfiguration vc = ViewConfiguration.get(this);
        swipeMinDistance = vc.getScaledPagingTouchSlop();
        swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();
        swipeMaxOffPath = vc.getScaledTouchSlop()*5;


        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };


        View view = getWindow().getDecorView().findViewById(android.R.id.content);
        // Do this for each view added to the grid
        view.setOnClickListener(MainActivity.this);
        view.setOnTouchListener(gestureListener);


        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromFile(this, XMLFilename); // getting XML
        if (xml == null) {
            mLog.print(ILogger.ERROR, TAG, "Can't parse xml file " + XMLFilename);
            finish();
        }

        Document doc = parser.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_CARD);


        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        cards = new ArrayList<Card>();
        // looping through all item nodes <card>     
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String position = parser.getValue(e, KEY_POSITION);
            String letter = parser.getValue(e, KEY_LETTER);
            String snd_letter = parser.getValue(e, KEY_SND_LETTER);
            String img = parser.getValue(e, KEY_IMG);
            String snd_img = parser.getValue(e, KEY_SND_IMG);
            String desc = parser.getValue(e, KEY_DESC);

            Integer snd_letter_id = loadSound(snd_letter, soundPool, 1);
            Integer snd_img_id = loadSound(snd_img, soundPool, 0);
            Drawable img_d = loadDrawable(img);


            Card card = new Card(i, position, letter, snd_letter_id, img_d, snd_img_id, desc);

            card.log(TAG);
            cards.add(card);

        }

        Card card = cards.get(0);
        imageView.setImageDrawable(card.img_d);
        setDescription(card.desc);
        textViewLetter.setText(card.letter);
        playLetter();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                playDescription();
            }
        }, 800);


    }

    private Drawable loadDrawable(String filename) {
        try {
            filename = "img/" + filename;
            return Drawable.createFromStream(this.getAssets().open(filename), null);
        } catch (IOException ex) {
            mLog.print(TAG, "Can't open file " + filename);
            return null;
        }
    }

    private Integer loadSound(String filename, SoundPool soundPool, int priority) {
        Integer sound_id = -1;
        if (!filename.equals("")) {
            try {
                filename = "snd/" + filename;
                AssetFileDescriptor descriptor = this.getAssets().openFd(filename);
                sound_id = soundPool.load(descriptor, priority);
            } catch (IOException ex) {
                mLog.print(TAG, "Can't open file " + filename);
            }
        }
        return sound_id;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStart() {
        super.onStart();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    protected void playDescription() {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //float volume = streamVolumeCurrent / streamVolumeMax;

        Card card = cards.get(index);
        soundPool.play(card.snd_img_id, 1, 1, 1, 0, 1);

//        switch (index) {
//            case 0:
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.begemot);
//                mp.start();
//                break;
//            case 1:
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.begemot);
//                mp.start();
//                break;
//            case 2:
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.begemot);
//                mp.start();
//                break;
//        }
    }

    protected void playLetter() {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //float volume = streamVolumeCurrent / streamVolumeMax;

        Card card = cards.get(index);
        soundPool.play(card.snd_letter_id, 1, 1, 1, 0, 1);

//        soundPool.play(soundsMap.get(index), 1, 1, 1, 0, 1);


//        switch (index) {
//            case 0:
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.rus_b);
//                mp.start();
//                break;
//            case 1:
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.rus_b);
//                mp.start();
//                break;
//            case 2:
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.rus_b);
//                mp.start();
//                break;
//        }
    }

    protected void setDescription(String desc) {
        //String mystring = getResources().getString(id);
        Spannable WordToSpan = new SpannableString(desc);
        WordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.letter_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewDecsription.setText(WordToSpan);
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            mLog.print(TAG, "In onFling");
//            Toast.makeText(MainActivity.this, "Please enter a valid number", Toast.LENGTH_LONG).show();

            try {
                if (Math.abs(e1.getY() - e2.getY()) > swipeMaxOffPath) {
//                    mLog.print(TAG, String.format("%f,%f,%d", e1.getY(), e2.getY(), swipeMaxOffPath));
//                    mLog.print(TAG, String.format("e1.getY %.1f, e2.getY: %.1f, swipeMaxOfPath: %.1f, abs_diff: %.1f", e1.getY(), e2.getY(), swipeMaxOffPath, Math.abs(e1.getY() - e2.getY())));
                    return false;
                }
                // right to left swipe
                if(e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
//                    Toast.makeText(MainActivity.this, "Left Swipe", Toast.LENGTH_SHORT).show();
                    index += 1;
                    if (index == max_index + 1)
                        index = 0;
                }  else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
//                    Toast.makeText(MainActivity.this, "Right Swipe", Toast.LENGTH_SHORT).show();
                    index -= 1;
                    if (index == -1)
                        index = max_index;
                }

                mLog.print(TAG, String.format("index %d", index));
                Card card = cards.get(index);
                imageView.setImageDrawable(card.img_d);
                setDescription(card.desc);
                textViewLetter.setText(card.letter);
                playLetter();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        playDescription();
                    }
                }, 800);

//                switch (index) {
//                    case 0:
//                        imageView.setImageResource(R.drawable.arbuz);
////                        textViewDecsription.setText(R.string.desc_rus1);
//                        //String text = "<font color=#ffcc00>А</font> <font color=#0000FF>рбуз</font>";
//                        //textViewLetter.setText(Html.fromHtml(text));
////                        String mystring = getResources().getString(R.string.desc_rus1);
////                        Spannable WordToSpan = new SpannableString(mystring);
////                        WordToSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////                        textViewDecsription.setText(WordToSpan);
//                        setDescription(R.string.desc_rus1);
//
//                        textViewLetter.setText(R.string.letter_rus1);
//
//                        playLetter();
////                        playDescription();
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                playDescription();
//                            }
//                        }, 800);
//
//
//                        break;
//                    case 1:
//                        imageView.setImageResource(R.drawable.begemot);
//                        //textViewDecsription.setText(R.string.desc_rus2);
//                        setDescription(R.string.desc_rus2);
//                        textViewLetter.setText(R.string.letter_rus2);
//
//                        playLetter();
//                        handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                playDescription();
//                            }
//                        }, 800);
////                        mp = MediaPlayer.create(getApplicationContext(), R.raw.rus_b);
////                        mp.start();
////
////                        mp = MediaPlayer.create(getApplicationContext(), R.raw.begemot);
////                        mp.start();
//
//                        break;
//                    case 2:
//                        imageView.setImageResource(R.drawable.gepard);
//                        //textViewDecsription.setText(R.string.desc_rus3);
//                        setDescription(R.string.desc_rus3);
//                        textViewLetter.setText(R.string.letter_rus3);
//
//                        playLetter();
//                        handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                playDescription();
//                            }
//                        }, 800);
//
////                        playDescription();
//
//                        break;
//                }
//
            } catch (Exception e) {
                mLog.printStackTrace(e);
                // nothing
            }
            return false;
        }

        public boolean onDown(View v) {
            return true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
