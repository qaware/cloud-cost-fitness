/*
   ________                __   ______           __     ______            _                      _
  / ____/ /___  __  ______/ /  / ____/___  _____/ /_   / ____/___  ____ _(_)___  ___  ___  _____(_)___  ____ _
 / /   / / __ \/ / / / __  /  / /   / __ \/ ___/ __/  / __/ / __ \/ __ `/ / __ \/ _ \/ _ \/ ___/ / __ \/ __ `/
/ /___/ / /_/ / /_/ / /_/ /  / /___/ /_/ (__  ) /_   / /___/ / / / /_/ / / / / /  __/  __/ /  / / / / / /_/ /
\____/_/\____/\__,_/\__,_/   \____/\____/____/\__/  /_____/_/ /_/\__, /_/_/ /_/\___/\___/_/  /_/_/ /_/\__, /
                                                                /____/                               /____/
 */
package de.qaware.cloud;

import java.util.ServiceLoader;

/**
 * The cloud provider enum used to get CostExplorer instances.
 */
public enum CloudProvider {
    AMAZON_AWS, GOOGLE_CLOUD, MICROSOFT_AZURE;

    /**
     * Return a new CostExplorer instance.
     * 
     * @return a cost explorer
     * @throws UnsupportedOperationException if no cost explorer could be found
     */
    public CostExplorer getCostExplorer() {
        ServiceLoader<CostExplorer> loader = ServiceLoader.load(CostExplorer.class);
        
        // this here only allows one implementation on the classpath
        return loader.findFirst().orElseThrow(UnsupportedOperationException::new);
    }
}
