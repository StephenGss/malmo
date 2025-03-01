// --------------------------------------------------------------------------------------------------
//  Copyright (c) 2016 Microsoft Corporation
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
//  associated documentation files (the "Software"), to deal in the Software without restriction,
//  including without limitation the rights to use, copy, modify, merge, publish, distribute,
//  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all copies or
//  substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
//  NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
//  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// --------------------------------------------------------------------------------------------------

package com.microsoft.Malmo.MissionHandlers;

import java.util.Random;

import com.microsoft.Malmo.MissionHandlerInterfaces.IWorldGenerator;
import com.microsoft.Malmo.Schemas.DefaultWorldGenerator;
import com.microsoft.Malmo.Schemas.MissionInit;
import com.microsoft.Malmo.Utils.MapFileHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;

public class DefaultWorldGeneratorImplementation extends HandlerBase implements IWorldGenerator
{
	DefaultWorldGenerator dwparams;
	
	@Override
	public boolean parseParameters(Object params)
	{
		if (params == null || !(params instanceof DefaultWorldGenerator))
			return false;
		
		this.dwparams = (DefaultWorldGenerator)params;
		return true;
	}
	
    public static long getWorldSeedFromString(String seedString)
    {
        // This seed logic mirrors the Minecraft code in GuiCreateWorld.actionPerformed:
        long seed = (new Random()).nextLong();
        if (seedString != null && !seedString.isEmpty())
        {
            try
            {
                long i = Long.parseLong(seedString);
                if (i != 0L)
                    seed = i;
            }
            catch (NumberFormatException numberformatexception)
            {
                seed = (long)seedString.hashCode();
            }
        }
        return seed;
    }

	@Override
    public boolean createWorld(MissionInit missionInit)
    {
        long seed = getWorldSeedFromString(this.dwparams.getSeed());
        WorldType.worldTypes[0].onGUICreateWorldPress();
        WorldSettings worldsettings = new WorldSettings(seed, GameType.SURVIVAL, true, false, WorldType.worldTypes[0]);
        worldsettings.enableCommands();
        // Create a filename for this map - we use the time stamp to make sure it is different from other worlds, otherwise no new world
        // will be created, it will simply load the old one.
        return MapFileHelper.createAndLaunchWorld(worldsettings, this.dwparams.isDestroyAfterUse());
    }

    @Override
    public boolean shouldCreateWorld(MissionInit missionInit, World world)
    {
        if (this.dwparams != null && this.dwparams.isForceReset())
            return true;
        
    	if (Minecraft.getMinecraft().theWorld == null || world == null)
            return true;    // Definitely need to create a world if there isn't one in existence!

        String genOptions = world.getWorldInfo().getGeneratorOptions();
        if (genOptions != null && !genOptions.isEmpty())
        	return true;	// Default world has no generator options.

        return false;
    }

    @Override
    public String getErrorDetails()
    {
        return "";  // Don't currently have any error exit points.
    }

}