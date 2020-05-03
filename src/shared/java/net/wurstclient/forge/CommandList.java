/*
 * Copyright (C) 2017 - 2019 | Wurst-Imperium | All rights reserved.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.forge;

import net.wurstclient.forge.commands.*;
import net.wurstclient.forge.compatibility.WCommandList;

public final class CommandList extends WCommandList
{
	public final HUDCmd hudCmd=register(new HUDCmd());
	public final RegCmd regCmd=register(new RegCmd());
	public final UpdateCmd updateCmd=register(new UpdateCmd());
	public final UnlockCmd unlockCmd=register(new UnlockCmd());
	public final KickCmd kickCmd=register(new KickCmd());
	public final EnemyCmd enemyCmd=register(new EnemyCmd());
	public final FriendsCmd friendsCmd =register(new FriendsCmd());
	public final DamageCmd damageCmd =register(new DamageCmd());
	public final PositionCmd positionCmd =register(new PositionCmd());
	public final BindsCmd bindsCmd = register(new BindsCmd());
	public final ClearCmd clearCmd = register(new ClearCmd());
	public final GmCmd gmCmd = register(new GmCmd());
	public final HelpCmd helpCmd = register(new HelpCmd());
	public final SayCmd sayCmd = register(new SayCmd());
	public final SetCheckboxCmd setCheckboxCmd = register(new SetCheckboxCmd());
	public final SetEnumCmd setEnumCmd = register(new SetEnumCmd());
	public final SetSliderCmd setSliderCmd = register(new SetSliderCmd());
	public final TCmd tCmd = register(new TCmd());
	public final TacoCmd tacoCmd = register(new TacoCmd());
	public final VClipCmd vClipCmd = register(new VClipCmd());
	public final VrTweaksCmd vrTweaksCmd = register(new VrTweaksCmd());
}
