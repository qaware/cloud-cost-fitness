/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cce.aws;

import de.qaware.cce.Usage;

/**
 * Usage fields for the AWS API
 */
public enum AwsUsage implements Usage {
    DYNAMO_DB_TRANSFER_INTERNET_IN("DDB: Data Transfer - Internet (IN)"),
    DYNAMO_DB_TRANSFER_INTERNET_OUT("DDB: Data Transfer - Internet (Out)"),
    DYNAMO_DB_INDEXED_DATA("DDB: Indexed Data Storage"),
    DYNAMO_DB_READ_CAPACITY("DDB: Provisioned Throughput Capacity - Read"),
    DYNAMO_DB_WRITE_CAPACITY("DDB: Provisioned Throughput Capacity - Write"),
    EC2_TRANSFER_CLOUD_FRONT_OUT("EC2: Data Transfer - CloudFront (Out)"),
    EC2_TRANSFER_AZ("EC2: Data Transfer - Inter AZ"),
    EC2_TRANSFER_INTERNET_IN("EC2: Data Transfer - Internet (In)"),
    EC2_TRANSFER_INTERNET_OUT("EC2: Data Transfer - Internet (Out)"),
    EC2_TRANSFER_REGION_IN("EC2: Data Transfer - Region to Region (In)"),
    EC2_TRANSFER_REGION_OUT("EC2: Data Transfer - Region to Region (Out)"),
    EBS_SSD("EC2: EBS - SSD(gp2)"),
    EBS_SNAPSHOTS("EC2: EBS - Snapshots"),
    EC2_TRANSFER_CLOUD_FRONT_IN("EC2: EC2 Data Transfer - CloudFront (In)"),
    LOAD_BALANCER_LCU_RUNNING_HOURS("EC2: ELB - LCU Running Hours"),
    LOAD_BALANCER_RUNNING_HOURS("EC2: ELB - Running Hours"),
    ELASTIC_IP_IDLE_ADDRESS("EC2: Elastic IP - Idle Address"),
    NAT_GATEWAY_PROCESSED_DATA("EC2: NAT Gateway - Data Processed"),
    NAT_GATEWAY_RUNNING_HOURS("EC2: NAT Gateway - Running Hours"),
    EC2_RUNNING_HOURS("EC2: Running Hours"),
    ELASTIC_SEARCH_RUNNING_HOURS("Elasticsearch: Running Hours"),
    S3_API_REQUESTS("S3: API Requests - Standard"),
    S3_TRANSFER_INTERNET_IN("S3: Data Transfer - Internet (In)"),
    S3_TRANSFER_INTERNET_OUT("S3: Data Transfer - Internet (Out)"),
    S3_TRANSFER_REGION_OUT("S3: Data Transfer - Region to Region (Out)"),
    S3_STORAGE("S3: Storage - Standard");

    private final String usage;

    AwsUsage(String usage) {
        this.usage = usage;
    }

    public String toString() {
        return usage;
    }
}
