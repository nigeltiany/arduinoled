package com.tiany.nigel.arduinoled.paid;

import android.provider.BaseColumns;

/**
 * Created by Nigel on 12/18/2015.
 */

public final class Color_columns implements BaseColumns {
    // This class cannot be instantiated
    private Color_columns () {}

    /**
     * The table name of animals = "animals"
     */
    public static final String TABLENAME = "all_colors";

    /**
     * The title of the animal
     * <P>Type: TEXT</P>
     */
    public static final String id = "id";

    /**
     * The color of the animal
     * <P>Type: TEXT</P>
     */

    public static final String hex = "hex";

    public static final String red = "red";

    public static final String green = "green";

    public static final String blue = "blue";

    public static final String color_name = "color_name";

}
