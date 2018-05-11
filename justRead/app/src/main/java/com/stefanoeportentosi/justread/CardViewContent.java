package com.stefanoeportentosi.justread;

/**
 * Created by stefanoeportentosi on 18/05/17.
 */

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stefanoeportentosi.justread.Database.DBSTORE;

import java.util.List;

public class CardViewContent {


    private Context context;
    private Double lat;
    private Double lng;
    private Integer id;
    private Float radius;

    public CardViewContent(Context context) {
        this.context=context;
    }

    public void getCardViewproperties (Integer id) {
        try {
            DBSTORE db = new DBSTORE(context);
            List<Luoghi> luoghi = db.getLuogo(id);
//            View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) ((Activity) context).findViewById(android.R.id.content)).getChildAt(0);
            //TextView titolo = (TextView) viewGroup.findViewById(R.id.titolo_card);
            //TextView corpo = (TextView) viewGroup.findViewById(R.id.corpo_card);
            Luoghi luogo = luoghi.get(0);
            //titolo.setText(luogo.getTitolo());
            //corpo.setText((luogo.getTitolo().toString()+ " "+luogo.getLat().toString()+ " "+luogo.getLng().toString()+" "+luogo.getRadius().toString()+"\n"+luogo.getNote()));


        }catch (NullPointerException e){}
    }
}