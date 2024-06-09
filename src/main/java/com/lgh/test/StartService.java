package com.lgh.test;

import com.beust.jcommander.Parameter;
import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v16.common.ImageAsset;
import com.google.ads.googleads.v16.enums.AdGroupStatusEnum;
import com.google.ads.googleads.v16.enums.AdGroupTypeEnum;
import com.google.ads.googleads.v16.enums.AssetTypeEnum;
import com.google.ads.googleads.v16.errors.GoogleAdsError;
import com.google.ads.googleads.v16.errors.GoogleAdsException;
import com.google.ads.googleads.v16.resources.AdGroup;
import com.google.ads.googleads.v16.resources.Asset;
import com.google.ads.googleads.v16.services.*;
import com.google.ads.googleads.v16.utils.ResourceNames;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.google.protobuf.ByteString;
import com.lgh.test.utils.ArgumentNames;
import com.lgh.test.utils.CodeSampleParams;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.lgh.test.utils.CodeSampleHelper.getPrintableDateTime;

public class StartService {

    private static class Params extends CodeSampleParams {
        @Parameter(names = ArgumentNames.CUSTOMER_ID, required = true)
        private Long customerId;
        @Parameter(names = ArgumentNames.CAMPAIGN_ID, required = true)
        private Long campaignId;

    }


    public void startMain(String imageurl) throws IOException {

        Params params = new Params();


        GoogleAdsClient googleAdsClient = null;
        try {
            googleAdsClient = GoogleAdsClient.newBuilder().fromPropertiesFile().build();
        } catch (FileNotFoundException fnfe) {
            System.err.printf(
                    "Failed to load GoogleAdsClient configuration from file. Exception: %s%n", fnfe);
            System.exit(1);
        } catch (IOException ioe) {
            System.err.printf("Failed to create GoogleAdsClient. Exception: %s%n", ioe);
            System.exit(1);
        }

        try {
            new StartService().addGroup(googleAdsClient, params.customerId,params.campaignId);
            new StartService().addPicAssert(googleAdsClient, params.customerId,imageurl);
        } catch (GoogleAdsException gae) {
            // GoogleAdsException is the base class for most exceptions thrown by an API request.
            // Instances of this exception have a message and a GoogleAdsFailure that contains a
            // collection of GoogleAdsErrors that indicate the underlying causes of the
            // GoogleAdsException.
            System.err.printf(
                    "Request ID %s failed due to GoogleAdsException. Underlying errors:%n",
                    gae.getRequestId());
            int i = 0;
            for (GoogleAdsError googleAdsError : gae.getGoogleAdsFailure().getErrorsList()) {
                System.err.printf("  Error %d: %s%n", i++, googleAdsError);
            }
            System.exit(1);
        }
    }


    private void addGroup(GoogleAdsClient googleAdsClient, long customerId, long campaignId) {
        String campaignResourceName = ResourceNames.campaign(customerId, campaignId);
        // Creates an ad group, setting an optional CPC value.
        AdGroup adGroup1 =
                AdGroup.newBuilder()
                        .setName("Earth to Mars Cruises #" + getPrintableDateTime())
                        .setStatus(AdGroupStatusEnum.AdGroupStatus.ENABLED)
                        .setCampaign(campaignResourceName)
                        .setType(AdGroupTypeEnum.AdGroupType.SEARCH_STANDARD)
                        .setCpcBidMicros(10_000_000L)
                        .build();
        List<AdGroupOperation> operations = new ArrayList<>();
        operations.add(AdGroupOperation.newBuilder().setCreate(adGroup1).build());

        try (AdGroupServiceClient adGroupServiceClient =
                     googleAdsClient.getLatestVersion().createAdGroupServiceClient()) {
            MutateAdGroupsResponse response =
                    adGroupServiceClient.mutateAdGroups(Long.toString(customerId), operations);
            System.out.printf("Added %d ad groups:%n", response.getResultsCount());
            for (MutateAdGroupResult result : response.getResultsList()) {
                System.out.println(result.getResourceName());
            }
        }
    }
    private void addPicAssert(GoogleAdsClient googleAdsClient, long customerId,String imageurl) throws IOException {
        byte[] imageData = ByteStreams.toByteArray(new URL(imageurl).openStream());

        // Create the image asset.
        ImageAsset imageAsset = ImageAsset.newBuilder().setData(ByteString.copyFrom(imageData)).build();

        // Creates an asset.
        Asset asset =
                Asset.newBuilder()
                        // Provide a unique friendly name to identify your asset.
                        // When there is an existing image asset with the same content but a different name, the
                        // new name will be dropped silently.
                        .setName("Marketing Image")
                        .setType(AssetTypeEnum.AssetType.IMAGE)
                        .setImageAsset(imageAsset)
                        .build();

        // Creates the operation.
        AssetOperation operation = AssetOperation.newBuilder().setCreate(asset).build();

        // Creates the service client.
        try (AssetServiceClient assetServiceClient =
                     googleAdsClient.getLatestVersion().createAssetServiceClient()) {
            // Issues a mutate request to add the asset.
            MutateAssetsResponse response =
                    assetServiceClient.mutateAssets(Long.toString(customerId), ImmutableList.of(operation));
            // Prints the result.
            System.out.printf(
                    "The image asset with resource name '%s' was created.%n",
                    response.getResults(0).getResourceName());
        }
    }
}