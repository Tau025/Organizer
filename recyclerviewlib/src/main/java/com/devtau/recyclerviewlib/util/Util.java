package com.devtau.recyclerviewlib.util;

import android.content.Context;
import android.content.res.Resources;
import com.devtau.recyclerviewlib.R;
import java.util.ArrayList;

public abstract class Util {
    public static ArrayList<String> getDefaultComparatorsNames(Context context) {
        Resources res = context.getResources();
        ArrayList<String> comparatorsNames = new ArrayList<>();
        comparatorsNames.add(res.getString(R.string.first_fresh));
        comparatorsNames.add(res.getString(R.string.first_old));
        comparatorsNames.add(res.getString(R.string.first_higher_price));
        comparatorsNames.add(res.getString(R.string.first_lower_price));
        comparatorsNames.add(res.getString(R.string.alphabetical));
        comparatorsNames.add(res.getString(R.string.rev_alphabetical));
        return comparatorsNames;
    }
}
