package com.jewellbiz.android.jewellbiz.items;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResult implements Parcelable {
	
	public String articleId;
	
	public String title;
	
	public String overview;
	
	public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() {
		public SearchResult createFromParcel(Parcel in) {
			return new SearchResult(in);
		}
		@Override
		public SearchResult[] newArray(int size) {
			return new SearchResult[size];
		}
		
	};
	
	public SearchResult() {
    }
	
	public SearchResult(Parcel in) {
		articleId = in.readString();
        title = in.readString();
        overview = in.readString();
    }
	
	public SearchResult copy() {
        SearchResult copy = new SearchResult();
        copy.articleId = this.articleId;
        copy.title = this.title;
        copy.overview = this.overview;
        return copy;
    }

	@Override
	public int describeContents() {
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(articleId);
        dest.writeString(title);
        dest.writeString(overview);
		
	}

}
