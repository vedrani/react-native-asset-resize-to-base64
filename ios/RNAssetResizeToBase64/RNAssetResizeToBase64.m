#import <AssetsLibrary/AssetsLibrary.h>
#import <UIKit/UIKit.h>
#import "RNAssetResizeToBase64.h"
#include "ImageHelpers.h"

@implementation RNAssetResizeToBase64

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(
  assetToResizedBase64:(NSString *)imageUrl
  width:(float)width
  height:(float)height
  callback:(RCTResponseSenderBlock)callback)
{

  CGSize newSize = CGSizeMake(width, height);
  NSURL *url = [[NSURL alloc] initWithString:imageUrl];
  ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];

  [library assetForURL:url resultBlock:^(ALAsset *asset)
  {
    /*Get the image Representation*/
    ALAssetRepresentation *representation = [asset defaultRepresentation];
    CGImageRef imageRef = [representation fullScreenImage];

    /* Get the image data and scale it according to Width and Height */
    NSData *imagePngRep		= UIImagePNGRepresentation([UIImage imageWithCGImage:imageRef]);
    UIImage *imageUI		= [UIImage imageWithData:imagePngRep];

    CGFloat imgWidth = imageUI.size.width * imageUI.scale;
    CGFloat imgHeight = imageUI.size.height * imageUI.scale;
    CGFloat imgVericalMiddle = imgHeight / 2;
    CGRect cropRect = CGRectMake(
      0.0,
      imgVericalMiddle - imgWidth / 2,
      imgWidth,
      imgVericalMiddle + imgWidth / 2
    );
    CGImageRef imageCropRef = CGImageCreateWithImageInRect([imageUI CGImage], cropRect);
    UIImage *croppedImage = [UIImage imageWithCGImage:imageCropRef];
    CGImageRelease(imageCropRef);

    UIImage *scaledImage  = [croppedImage scaleToSize:newSize];

    /* Et the newImageData and transform it to base64 */
    NSData *ImageData = UIImageJPEGRepresentation(scaledImage, 1.0);
    NSString *base64Encoded = [ImageData base64EncodedStringWithOptions:0];

    callback(@[[NSNull null], base64Encoded]);
  }
  failureBlock:^(NSError *error)
  {
    NSLog(@"that didn't work %@", error);
    callback(@[error]);
  }];
}

@end
