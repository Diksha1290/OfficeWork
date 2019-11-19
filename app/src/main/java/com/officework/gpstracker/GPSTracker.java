package com.officework.gpstracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceShowGPSDialog;
import com.officework.utils.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Keep track of your current Position in terms of Lat long
 */
public class GPSTracker extends Service implements LocationListener {
    private final Context mContext;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private boolean canGetLocation = false;

    private Location location;
    public double latitude;
    public double longitude;
    private double altitude;
    private float accuracy;
    private float bearing;
    private float speed;
    private long time;
    private String provider;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; //10 metters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 6; // 1 minute

    protected LocationManager locationManager;
    public static boolean isDialogShowing = false;
    InterfaceAlertDissmiss listernerDialog;
    public static final int dialogCallBack = 111;
    InterfaceShowGPSDialog gpsDialogListerner;

    public GPSTracker(Context context, InterfaceAlertDissmiss listerner, InterfaceShowGPSDialog gpsDialog) {
        this.mContext = context;
        listernerDialog = listerner;
        gpsDialogListerner = gpsDialog;
        //getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS/network status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = Utilities.getInstance(mContext).isInternetWorking(mContext);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert(1);
            }
//            else if (!isNetworkEnabled) {
//                showSettingsAlert(1);}
            else {
                this.canGetLocation = true;
                //First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        updateGPSCoordinates(location);
                    }
                }
                //if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            updateGPSCoordinates(location);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void updateGPSCoordinates(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else if (location == null) {
            showSettingsAlert(0);
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get bearing
     */
    public String getProvider() {
        if (location != null) {
            provider = location.getProvider();
        }
        return provider;
    }

    /**
     * Function to get bearing
     */
    public long getTime() {
        if (location != null) {
            time = location.getTime();
        }
        return time;
    }

    /**
     * Function to get bearing
     */
    public float getBearing() {
        if (location != null) {
            bearing = location.getBearing();
        }
        return bearing;
    }

    /**
     * Function to get bearing
     */
    public float getSpeed() {
        if (location != null) {
            speed = location.getSpeed();
        }
        return speed;
    }

    /**
     * Function to get accuracy
     */
    public float getAccuracy() {
        if (location != null) {
            accuracy = location.getAccuracy();
        }
        return accuracy;
    }

    /**
     * Function to get altitude
     */
    public double getAltitude() {
        if (location != null) {
            altitude = location.getAltitude();
        }
        return altitude;
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert(int type) {
        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS Error");
        alertDialog.setMessage("GPS disabled! Please turn it on.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                isDialogShowing = false;
                listernerDialog.onButtonClick(false, dialogCallBack);
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                isDialogShowing = false;
                listernerDialog.onButtonClick(true, dialogCallBack);
            }
        });
        if (!isDialogShowing) {
            alertDialog.show();
            isDialogShowing = true;
        }*/
        if (!isDialogShowing) {
            gpsDialogListerner.onShowDialog(true, type);
            isDialogShowing = true;
        }
    }

    /**
     * Get list of address by latitude and longitude
     *
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context) {
        if (location != null) {
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                return addresses;
            } catch (IOException e) {

            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     *
     * @return null or addressLine
     */
    public String getAddressLine(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     *
     * @return null or locality
     */
    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        } else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     *
     * @return null or postalCode
     */
    public String getPostalCode(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();

            return postalCode;
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     *
     * @return null or postalCode
     */
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateGPSCoordinates(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean IsGPSEnabled() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            //getting GPS/network status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert(0);
                return false;
            }
//            else if (!isNetworkEnabled) {
//                showSettingsAlert(1);
//                return false;
//            }
        } catch (Exception e) {
            //showSettingsAlert();
            return false;
        }
        return true;
    }
}