
				/*
 *  OpenVPNALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package net.openvpn.als.input.validators;

 
/**
 * A validator for time in 24hour HH:MM format
 */
public class TimeValidator extends StringValidator {
    /**
	 * Regular expression for a time string in the format <i>HH:MM</i>
	 */
	final static String TIME_PATTERN = "^([0-1][0-9]|[2][0-3]):([0-5][0-9])$";

	/**
	 * Constructor.
	 */
	public TimeValidator() {
		super(1, 5, TIME_PATTERN, null, true);
	}
}
