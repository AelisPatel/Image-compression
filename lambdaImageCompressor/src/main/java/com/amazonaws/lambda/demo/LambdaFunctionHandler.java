package com.amazonaws.lambda.demo;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.AbstractPutObjectRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {
	BasicAWSCredentials creds = new BasicAWSCredentials("AKIAUV5VXTMMDNIKTRNI",
			"9JF0jKGISYDaTP0UGTAqpBF+W82e6kPWtAyXjpW5");

	private AmazonS3 amazonS3 = AmazonS3Client.builder().withRegion("us-east-1")
			.withCredentials(new AWSStaticCredentialsProvider(creds)).build();
	//private AmazonS3 amazonS3=AmazonS3ClientBuilder.standard().build();;

	 /*private AmazonS3 amazonS3;

	    public LambdaFunctionHandler() {
	        amazonS3 = AmazonS3ClientBuilder.standard().build();
	    }*/
	 public LambdaFunctionHandler() {}

	    // Test purpose only.
	    LambdaFunctionHandler(AmazonS3 amazonS3) {
	        this.amazonS3 = amazonS3;
	    }
@Override
	    public String handleRequest(S3Event o, Context context) {
		context.getLogger().log("Received event: " + o);
	        //S3EventNotification.S3EventNotificationRecord record = o.getRecords().get(0);

	        //S3EventNotification.S3Entity s3Entity = record.getS3();
	    	
	        /*final  String bucketName = s3Entity.getBucket().getName();
	        final String key = s3Entity.getObject().getKey();
	        final  String to_bucket="demobucket345";
	        System.out.println(bucketName);
	        System.out.println(key);*/
	        try  {
	        	S3EventNotificationRecord record = o.getRecords().get(0);
	        	String bucketName = record.getS3().getBucket().getName();
	        	// Object key may have spaces or unicode non-ASCII characters.
				String key = record.getS3().getObject().getKey().replace('+', ' ');
				System.out.println("srcbucketName:"+bucketName);
				System.out.println("srckey:"+key);
				key = URLDecoder.decode(key, "UTF-8");
				
				String s3Link1=amazonS3.getUrl(bucketName, key).toString();
				System.out.println("s3link1:"+s3Link1);
				String to_bucket = "s3demobucket09";

	        	S3Object object = amazonS3.getObject(new GetObjectRequest(bucketName, key));
	        	
	            Map<String, String> metadata =  object.getObjectMetadata().getUserMetadata();
	            if (metadata.get("compressed") == null){
	                metadata.put("compressed", "true");
	            } else {
	                return "Already Compressed";
	            }

	            ObjectMetadata objectMetadata = new ObjectMetadata();
	            objectMetadata.setUserMetadata(metadata);

	            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	            compress(object.getObjectContent(), byteArrayOutputStream);

	            objectMetadata.setContentLength(byteArrayOutputStream.size());

	            amazonS3.putObject(new PutObjectRequest(bucketName, key, new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), objectMetadata));
	            amazonS3.copyObject(bucketName, key, to_bucket, key);
	            System.out.println("---copied object successfully----\n");
	            String  s3Link = amazonS3.getUrl(to_bucket, key).toString();
	    		System.out.println("s3link:"+s3Link);
	            
				database d=new database();
				d.insert(bucketName,s3Link1,to_bucket,s3Link);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return "Failed: " + e.getMessage();
	        }

	        return "Success!";
	    }

	    public static void compress(S3ObjectInputStream inputStream, OutputStream outputStream) throws IOException {
	        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
	            IOUtils.copy(inputStream, byteArrayOutputStream);
	            byte[] bytes = byteArrayOutputStream.toByteArray();

	            ImageType imageType = InputStreamIdentifier.getImageType(new ByteArrayInputStream(bytes));
	            System.out.println("Input is: " + imageType);

	            switch (imageType) {
	                case PNG:
	                case JPG:
	                case GIF:
	                    compress(imageType, new ByteArrayInputStream(bytes), outputStream);
	                    break;
	            }
	        }
	    }

	    public static void compress(ImageType imageType, ByteArrayInputStream inputStream, OutputStream outputStream) throws IOException {
	        try {
	            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(imageType.getExtension());
	            ImageWriter writer = writers.next();

	            ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
	            writer.setOutput(ios);

	            ImageWriteParam param = writer.getDefaultWriteParam();
	            // Check if canWriteCompressed is true
	            if (param.canWriteCompressed()) {
	                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	                param.setCompressionQuality(0.75f);
	            }
	            // End of check
	            writer.write(null, new IIOImage(ImageIO.read(inputStream), null, null), param);
	        } finally {
	            inputStream.close();
	        }
	    }
	    

	}