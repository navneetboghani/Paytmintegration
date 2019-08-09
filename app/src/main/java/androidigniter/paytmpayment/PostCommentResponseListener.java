package androidigniter.paytmpayment;

import com.android.volley.VolleyError;

public interface PostCommentResponseListener {
    public void requestStarted();
    public void requestCompleted();
    public void requestEndedWithError(VolleyError error);
}
