package de.qaware.cce;

import de.qaware.cce.aws.AwsCostExplorer;

/**
 * Producer of a CostExplorer instance
 */
public class CloudCostExplorer {
    /**
     * Creates a new CostExplorer instance for the given cloud provider
     *
     * @param provider the cloud provider
     * @return the new inctance
     */
    public static CostExplorer forProvider(CloudProvider provider) {
        switch (provider) {
            case AMAZON_AWS:
                return new AwsCostExplorer();
            case GOOGLE_CLOUD:
            case MICROSOFT_AZURE:
                throw new IllegalStateException("Not yet implemented");
        }
        throw new IllegalStateException();
    }
}
