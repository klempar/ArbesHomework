package cz.janklempar;

import java.util.Date;

// Třída Interval pro reprezentaci časových intervalů a kontrolu překrývání
class CallInterval {
    private final Date start;
    private final Date end;

    public CallInterval(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public boolean overlapsWith(CallInterval intervalToCompare) {
        return this.start.before(intervalToCompare.end) && this.end.after(intervalToCompare.start);
    }
}