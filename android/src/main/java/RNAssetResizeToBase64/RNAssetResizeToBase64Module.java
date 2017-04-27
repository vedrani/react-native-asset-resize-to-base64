package RNAssetResizeToBase64;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.Map;
import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.provider.MediaStore;
import android.net.Uri;
import java.io.IOException;
import java.io.FileNotFoundException;

public class RNAssetResizeToBase64Module extends ReactContextBaseJavaModule
{
	Context context;

	public RNAssetResizeToBase64Module(ReactApplicationContext reactContext)
	{
		super(reactContext);
		this.context = (Context) reactContext;
	}

	@Override
	public String getName()
	{
		return "RNAssetResizeToBase64";
	}

	@ReactMethod
	public void assetToResizedBase64(String uri, int width, int height, Callback callback)
	{
		try
		{
			Bitmap image = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), Uri.parse(uri));
			if (image == null)
				callback.invoke("FAIL : uri: " + uri);
			else
				callback.invoke(null, makeConversion(image, width, height));
		}
		catch (IOException e)
		{
		}
  }

	private String makeConversion(Bitmap bitmap, int width, int height)
	{
    int imgWidth = bitmap.getWidth();
    int imgHeight = bitmap.getHeight();
    int imgVericalMiddle = imgHeight / 2;
    Bitmap croppedBitmap = Bitmap.createBitmap(
      bitmap,
      0,
      imgVericalMiddle - imgWidth / 2,
      imgWidth,
      imgWidth
    );

    Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, width, height, false);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		return Base64.encodeToString(byteArray, Base64.DEFAULT);
	}
}
