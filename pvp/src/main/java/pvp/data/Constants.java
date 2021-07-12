package pvp.data;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final List<String> JUNK = new ArrayList<>();

    public static final int PRAYER_TIMEOUT = 700;
    public static final int BARRAGE_GRAPHIC = 369;

    static {
        JUNK.add(" book");
        JUNK.add("maul");
//        JUNK.add("ballista");
        JUNK.add("arrow");
//        JUNK.add("agon javelin");
        JUNK.add(" helm");
        JUNK.add("ark bow");
    }
}
