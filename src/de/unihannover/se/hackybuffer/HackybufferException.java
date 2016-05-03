/**
    This file is part of Hackybuffer.

    Hackybuffer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Hackybuffer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Hackybuffer. If not, see <http://www.gnu.org/licenses/>.
 */

package de.unihannover.se.hackybuffer;

/**
 * Exception that occurs during writing of a sensor event.
 * Mainly wraps the low level exceptions to shield the clients from implementation details.
 */
public class HackybufferException extends Exception {

    private static final long serialVersionUID = 3886438610342568002L;

    public HackybufferException(Exception e) {
        super(e);
    }

}
