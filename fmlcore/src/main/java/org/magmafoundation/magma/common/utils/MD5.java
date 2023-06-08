/*
 * Magma Server
 * Copyright (C) 2019-2023.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Project: Magma
 *
 * @author Malcolm (M1lc0lm) / Hexeption / Mgazul
 * @date 03.07.2022 - 17:19
 */
public class MD5 {

	public static String getMd5(File path) {
		try {
			return String.format("%032x", new BigInteger(1, MessageDigest.getInstance("MD5").digest(Files.readAllBytes(path.toPath())))).toLowerCase();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getMd5(InputStream is) {
		try {
			return String.format("%032x", new BigInteger(1, new DigestInputStream(is, MessageDigest.getInstance("MD5")).getMessageDigest().digest())).toLowerCase();
		} catch (Exception e) {
			return null;
		}
	}

	// Check MD5
	public static boolean checkMD5(File file, String md5sum) throws IOException {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("[ERROR] MD5 algorithm not found");
			return false;
		}

		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[1024];
		int read;
		while ((read = fis.read(data)) != -1) {
			digest.update(data, 0, read);
		}
		fis.close();

		byte[] md5sumBytes = fromHexString(md5sum);
		byte[] bytes = digest.digest();

		for (int i = 0; i < md5sumBytes.length; i++) {
			if (md5sumBytes[i] != bytes[i]) {
				return false;
			}
		}

		return true;
	}

	// Convert Hex String to Byte Array
	private static byte[] fromHexString(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return bytes;
	}

	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis =  new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	public static byte[] createChecksum(InputStream fis) throws Exception {
		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(String filename) {
		try {
			byte[] b = createChecksum(filename);
			String result = "";

			for (int i = 0; i < b.length; i++) {
				result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
			}
			return result;
		} catch (Exception e){
			return null;
		}
	}

	public static String getMD5Checksum(InputStream fis) throws Exception {
		byte[] b = createChecksum(fis);
		String result = "";

		for (int i=0; i < b.length; i++) {
			result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

}
