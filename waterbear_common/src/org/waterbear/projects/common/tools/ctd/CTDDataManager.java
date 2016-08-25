package org.waterbear.projects.common.tools.ctd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.waterbear.core.exception.AutomationException;
import org.waterbear.core.exception.InvalidDataException;
import org.waterbear.core.utils.WBStringUtils;

import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;

public class CTDDataManager {

	private static final String NL = System.lineSeparator();
	private File modelFile;
	private File destDir;
	private String beanClassName;

	public CTDDataManager(String modelFilePath) {
		this.modelFile = new File(modelFilePath);
		destDir = new File(System.getProperty("user.dir"), "toolssrc/ctd/gen");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		beanClassName = WBStringUtils.capitalize(modelFile.getName().replace(
				".model", ""))
				+ "Bean";
	}

	public Object[][] loadData(Class beanClazz, String csvFilePath) {
		final String[] attrNames = getAttrNames();
		File csvFile = new File(csvFilePath);
		Reader in = null;
		CSVParser p = null;
		Object[][] items;
		try {
			in = new FileReader(csvFile.getPath());
			p = new CSVParser(in, CSVFormat.EXCEL.withHeader(attrNames));

			List<CSVRecord> records = p.getRecords();
			final long recordNumber = p.getRecordNumber() - 1;
			items = new Object[(int) recordNumber][1];
			for (int i = 1; i <= recordNumber; i++) {
				CSVRecord record = records.get(i);
				MethodAccess access = MethodAccess.get(beanClazz);
				Object bean = beanClazz.newInstance();
				for (String attrName : attrNames) {
					access.invoke(bean,
							"set" + WBStringUtils.capitalize(attrName),
							record.get(attrName));
				}
				items[i - 1] = new Object[] { bean };
			}
			return items;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				p.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void genBean() {
		List<Attribute> attributes = loadAttributes();
		StringBuilder sb = new StringBuilder();
		sb.append("public class ").append(beanClassName).append("{").append(NL);
		genFields(sb, attributes);
		genSetters(sb, attributes);
		genGetters(sb, attributes);
		genPrepare(sb, attributes);
		sb.append("}");
		writeFile(sb.toString());
	}

	private void genPrepare(StringBuilder sb, List<Attribute> attrList) {
		sb.append("public void prepare(){").append(NL);
		// BEGIN - generate the method body
		attrList.forEach((attr) -> {
			final String attrName = attr.getName();
			if (attr.getType().equals("String")) {
				sb.append("//" + attrName).append(NL);
				sb.append("switch(" + attrName + "){").append(NL);
				attr.getValues().forEach((s) -> {
					sb.append("case \"" + s + "\":").append(NL);
					sb.append(attrName + "=" + attrName + ";").append(NL);
					sb.append("break;").append(NL);
				});
				sb.append("default:").append(NL);
				sb.append(
						"throw new InvalidDataException(\"Unknown value [\" + "
								+ attrName + "+\"] for the attribute ["
								+ attrName + "].\");").append(NL);
				sb.append("}").append(NL);
			}
		});
		// END - generate the method body
		sb.append("}").append(NL);
	}

	private void writeFile(String str) {
		FileOutputStream out = null;
		try {
			File file = new File(destDir, beanClassName + ".java");
			if (!file.exists())
				file.createNewFile();
			out = new FileOutputStream(file, false);
			out.write(str.getBytes("utf-8"));
			System.out.println(file.getAbsolutePath()
					+ " was generated successfully.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void genFields(StringBuilder sb, List<Attribute> attrList) {
		attrList.forEach((attr) -> {
			sb.append("private ").append(attr.getType()).append(" ")
					.append(attr.getName()).append(";").append(NL);
		});
	}

	private void genGetters(StringBuilder sb, List<Attribute> attrList) {
		attrList.forEach((attr) -> {
			sb.append("public ").append(attr.getType()).append(" get")
					.append(WBStringUtils.capitalize(attr.getName()))
					.append("()").append("{").append(NL);
			sb.append("	return ").append(attr.getName()).append(";").append(NL);
			sb.append("}").append(NL);
		});

	}

	private void genSetters(StringBuilder sb, List<Attribute> attrList) {
		attrList.forEach((attr) -> {
			final String attrName = attr.getName();
			sb.append("public void ").append(" set")
					.append(WBStringUtils.capitalize(attrName)).append("(")
					.append(attr.getType()).append(" " + attrName + ")")
					.append("{").append(NL);
			// BEGIN - generate the method body
			sb.append("this." + attrName + "=" + attrName + ";").append(NL);
			// END - generate the method body
			sb.append("}").append(NL);
		});

	}

	private String[] getAttrNames() {
		List<Attribute> attrs = loadAttributes();
		List<String> attrNameList = new ArrayList<String>();
		attrs.forEach((attr) -> {
			attrNameList.add(attr.getName());
		});
		String[] attrNames = new String[attrNameList.size()];
		attrNameList.toArray(attrNames);
		return attrNames;
	}

	private List<Attribute> loadAttributes() {
		List<Attribute> attrList = new ArrayList<Attribute>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(modelFile));
			String line = reader.readLine();

			String name = null;
			String type = null;
			List<String> values = new ArrayList<String>();

			while (line != null) {
				if (line.startsWith("<attribute")) {
					String[] parts = line.split(" ");
					for (String part : parts) {
						if (part.startsWith("name=")) {
							name = part.substring(6, part.length() - 1);
						} else if (part.startsWith("type=")) {
							final String ctdAttrType = part.substring(6,
									part.length() - 2);
							type = toJavaFieldType(ctdAttrType);
						}
					}
				} else if (line.startsWith("<value")) {
					final String value = line.split("\"")[1];
					values.add(value);
				} else if (line.startsWith("</attribute>")) {
					attrList.add(new Attribute(name, type, values));
					name = null;
					type = null;
					values = new ArrayList<String>();
				} else if (line.startsWith("<restriction")) {
					break;
				}
				line = reader.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return attrList;
	}

	private String toJavaFieldType(final String ctdAttrType) {
		String javaType = null;
		switch (ctdAttrType) {
		case "STRING":
			javaType = "String";
			break;
		case "INT":
			javaType = "int";
			break;
		case "BOOLEAN":
			javaType = "boolean";
			break;
		default:
			throw new AutomationException("Unknown CTD attribute type ["
					+ ctdAttrType + "]");
		}
		return javaType;
	}

	class Attribute {
		private String name;
		private String type;
		private List<String> values;

		public Attribute(String name, String type, List<String> values) {
			super();
			this.name = name;
			this.type = type;
			this.values = values;
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}

		public List<String> getValues() {
			return values;
		}
	}
}
