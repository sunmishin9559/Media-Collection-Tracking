package application;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class MediaItem implements Serializable, Comparable<MediaItem> {

    private String title;
    private String format;
    private String loanedTo;
    private Date dateLoaned;

    public MediaItem(String title, String format) {
        this.title = title;
        this.format = format;
    }

    public void loan(String loanedTo, Date loanedOn) {
        this.loanedTo = loanedTo;
        this.dateLoaned = loanedOn;
    }

    public void returnItem() {
        this.loanedTo = null;
        this.dateLoaned = null;
    }

    @Override
    public String toString() {
        String response = title + " - " + format;

        if (loanedTo != null) {
            response += " (" + loanedTo + " on " + dateLoaned + ")";
        }

        response = response.replace("00:00:00 EDT ", "");
        return response;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.title);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MediaItem other = (MediaItem) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return true;
    }

    public String getLoanedTo() {
        return loanedTo;
    }

    public String getTitle() {
        return title;
    }

    public Date getLoanedOn() {
        return dateLoaned;
    }

    @Override
    public int compareTo(MediaItem otherMedia) {
        return this.getTitle().compareTo(otherMedia.getTitle());
    }
    

//    public class byDataCompare implements Comparator<MediaItem> {
//
//        @Override
//        public int compare(MediaItem t1, MediaItem t2) {
//
//            if (t1.getLoanedOn() == null) {
//                return -1;
//            } else if (t2.getLoanedOn() == null) {
//                return 1;
//            } else if (t1.getLoanedOn() == null && t2.getLoanedOn() == null) {
//                int tNum = t1.getTitle().compareTo(t2.getTitle());
//
//                if (tNum > 0) {
//                    return 1;
//                } else {
//                    return -1;
//                }
//            } else {
//                int dNum = t1.getLoanedOn().compareTo(t2.getLoanedOn());
//                
//                if(dNum > 0){
//                    return 1;
//                }else if(dNum < 0){
//                    return -1;
//                }else{
//                    return 0;
//                }
//            }
//            
//            
//        }
//    }

}
