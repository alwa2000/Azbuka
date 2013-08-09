package alexeli.azbuka;

import android.graphics.drawable.Drawable;

import alexeli.azbuka.logger.ILogger;
import alexeli.azbuka.logger.LoggerFactory;

/**
 * The class holds full information about a card
 */
public class Card {

    //private static final String TAG = "Card";
    private static ILogger mLog = LoggerFactory.create();

    public int id;
    public String position;
    public String letter;
    public Integer snd_letter_id;
    public Drawable img_d;
    public Integer snd_img_id;
    public String desc;

    public Card(int lid, String lposition, String lletter, Integer lsnd_letter_id, Drawable limg_d, Integer lsnd_img_id, String ldesc) {
        id = lid;
        position = lposition;
        letter = lletter;
        snd_letter_id = lsnd_letter_id;
        img_d = limg_d;
        snd_img_id = lsnd_img_id;
        desc = ldesc;
    }

    public void log(String tag) {
        mLog.print(tag, "" + id + " " +
                position + " " +
                letter + " " +
                snd_letter_id + " " +
                img_d + " " +
                snd_img_id + " " +
                desc);
    }

}
