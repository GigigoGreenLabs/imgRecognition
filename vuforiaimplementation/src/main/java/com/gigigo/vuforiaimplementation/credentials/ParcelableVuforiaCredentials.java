package com.gigigo.vuforiaimplementation.credentials;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sergio Martinez Rodriguez
 * Date 29/4/16.
 */
public class ParcelableVuforiaCredentials implements Parcelable{

  private final String licenseKey;
  private final String clientAccessKey;
  private final String clientSecretKey;
  private final String serverAccessKey;
  private final String serverSecretKey;

  public ParcelableVuforiaCredentials(String licenseKey, String clientAccessKey,
      String clientSecretKey, String serverAccessKey, String serverSecretKey) {

    this.licenseKey = licenseKey;
    this.clientAccessKey = clientAccessKey;
    this.clientSecretKey = clientSecretKey;
    this.serverAccessKey = serverAccessKey;
    this.serverSecretKey = serverSecretKey;

  }

  public String getLicenseKey() {
    return licenseKey;
  }

  public String getClientAccessKey() {
    return clientAccessKey;
  }

  public String getClientSecretKey() {
    return clientSecretKey;
  }

  public String getServerAccessKey() {
    return serverAccessKey;
  }

  public String getServerSecretKey() {
    return serverSecretKey;
  }

  protected ParcelableVuforiaCredentials(Parcel in) {
    licenseKey = in.readString();
    clientAccessKey = in.readString();
    clientSecretKey = in.readString();
    serverAccessKey = in.readString();
    serverSecretKey = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(licenseKey);
    dest.writeString(clientAccessKey);
    dest.writeString(clientSecretKey);
    dest.writeString(serverAccessKey);
    dest.writeString(serverSecretKey);
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<ParcelableVuforiaCredentials> CREATOR = new Parcelable.Creator<ParcelableVuforiaCredentials>() {
    @Override
    public ParcelableVuforiaCredentials createFromParcel(Parcel in) {
      return new ParcelableVuforiaCredentials(in);
    }

    @Override
    public ParcelableVuforiaCredentials[] newArray(int size) {
      return new ParcelableVuforiaCredentials[size];
    }
  };
}
