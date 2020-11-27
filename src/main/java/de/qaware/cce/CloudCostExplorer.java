/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
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
