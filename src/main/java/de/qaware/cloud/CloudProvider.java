/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cloud;

import de.qaware.cloud.aws.AwsCostExplorer;

/**
 * The cloud provider
 */
public enum CloudProvider {
    AMAZON_AWS {
        public CostExplorer getCostExplorer() {
            return new AwsCostExplorer();
        }
    },
    GOOGLE_CLOUD {
        public CostExplorer getCostExplorer() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    },
    MICROSOFT_AZURE {
        public CostExplorer getCostExplorer() {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    };

    /**
     * Return a new CostExplorer instance.
     * 
     * @return a cost explorer
     */
    public abstract CostExplorer getCostExplorer();
}
