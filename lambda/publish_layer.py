import boto3

regions = [
    "us-east-1",
    "us-east-2",
    "us-west-1",
    "us-west-2",
    "ap-south-1",
    "ap-northeast-1",
    "ap-northeast-2",    
    "ap-southeast-1",
    "ap-southeast-2",
    "ca-central-1",
    "eu-central-1",
    "eu-west-1",
    "eu-west-2",
    "eu-west-3",
    "eu-north-1",
    "sa-east-1"
]

layer_name = 'kotlin-native-runtime'
layer_bytes = open('./kn-runtime-layer.zip', 'rb').read()

for region in regions:
    print("Publishing in region: " + region)
    client = boto3.client('lambda', region_name=region)
    try:
        client.delete_layer_version(
            LayerName=layer_name,
            VersionNumber=1
        )  
    except:
        pass
    layer_response = client.publish_layer_version(
        LayerName=layer_name,
        Description='Lambda Runtime bootstrap for Kotlin/Native',
        Content={
            'ZipFile': layer_bytes
        },
        CompatibleRuntimes=['provided'],
        LicenseInfo='MIT'
    )    
    
    layer_version_response = client.add_layer_version_permission(
        LayerName=layer_name,
        VersionNumber=layer_response['Version'],
        StatementId='publish',
        Action='lambda:GetLayerVersion',
        Principal='*'
    )    