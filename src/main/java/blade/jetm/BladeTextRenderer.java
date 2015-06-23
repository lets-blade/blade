package blade.jetm;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import etm.core.aggregation.Aggregate;
import etm.core.monitor.EtmException;
import etm.core.renderer.MeasurementRenderer;

@SuppressWarnings("rawtypes")
public class BladeTextRenderer implements MeasurementRenderer {

	private final NumberFormat timeFormatter;
	private final Writer writer;
	private final String separator = System.getProperty("line.separator");

	public BladeTextRenderer() {
		this(new OutputStreamWriter(System.out), Locale.getDefault());
	}

	public BladeTextRenderer(Writer aWriter) {
		this(aWriter, Locale.getDefault());
	}
	
	public BladeTextRenderer(Locale locale) {
		this(new OutputStreamWriter(System.out), locale);
	}

	public BladeTextRenderer(Writer aWriter, Locale aLocale) {
		writer = aWriter;
		timeFormatter = NumberFormat.getNumberInstance(aLocale);
		timeFormatter.setMaximumFractionDigits(3);
		timeFormatter.setMinimumFractionDigits(3);
		timeFormatter.setGroupingUsed(true);
	}

	public BladeTextRenderer(NumberFormat aTimeFormatter) {
		writer = new OutputStreamWriter(System.out);
		timeFormatter = aTimeFormatter;
	}

	public BladeTextRenderer(Writer aWriter, NumberFormat aTimeFormatter) {
		writer = aWriter;
		timeFormatter = aTimeFormatter;
	}
	
	@SuppressWarnings("unchecked")
	public void render(Map points) {

		Results results = new Results(points);

		try {
			results.render(writer);
			writer.flush();
		} catch (IOException e) {
			throw new EtmException("Unable to write to writer: " + e);
		}
	}

	class Results {

		private Column nameColumn = new Column("监测点");
		private Column numberColumn = new Column("执行次数");
		private Column avgColumn = new Column("平均时长");
		private Column minColumn = new Column("最小时长");
		private Column maxColumn = new Column("最大时长");
		private Column totalColumn = new Column("总时间");

		public <K, V> Results(Map<K, V> points) {
			Map<K, V> map = new TreeMap<K, V>(points);
			Iterator<V> iterator = map.values().iterator();
			if (iterator.hasNext()) {
				Aggregate point = (Aggregate) iterator.next();
				addTopLevel(point);
			}
			// for (Iterator iterator = map.values().iterator();
			// iterator.hasNext();) {
			// Aggregate point = (Aggregate) iterator.next();
			// addTopLevel(point);
			// }
		}

		public void addTopLevel(Aggregate aAggregate) {
			addLine(0, aAggregate);

			if (aAggregate.hasChilds()) {
				addNested(1, aAggregate.getChilds());
			}
			addSeparator();
		}

		public void addNested(int nestingLevel, Map childs) {
			for (Iterator iterator = childs.values().iterator(); iterator
					.hasNext();) {
				Aggregate point = (Aggregate) iterator.next();
				addLine(nestingLevel, point);
				if (point.hasChilds()) {
					addNested(nestingLevel + 1, point.getChilds());
				}
			}
		}

		private void addSeparator() {
			nameColumn.addEntry(new SeparatorEntry());
			numberColumn.addEntry(new SeparatorEntry());
			avgColumn.addEntry(new SeparatorEntry());
			minColumn.addEntry(new SeparatorEntry());
			maxColumn.addEntry(new SeparatorEntry());
			totalColumn.addEntry(new SeparatorEntry());
		}

		public void addLine(int nestingLevel, Aggregate aAggregate) {
			nameColumn.addEntry(new NestedEntry(nestingLevel, aAggregate
					.getName()));
			numberColumn.addEntry(new RightAlignedEntry(String
					.valueOf(aAggregate.getMeasurements())));
			avgColumn.addEntry(new RightAlignedEntry(timeFormatter
					.format(aAggregate.getAverage())));
			minColumn.addEntry(new RightAlignedEntry(timeFormatter
					.format(aAggregate.getMin())));
			maxColumn.addEntry(new RightAlignedEntry(timeFormatter
					.format(aAggregate.getMax())));
			totalColumn.addEntry(new RightAlignedEntry(timeFormatter
					.format(aAggregate.getTotal())));
		}

		public void render(Writer writer) throws IOException {
			Iterator nameIt = nameColumn.iterator();
			Iterator numberIt = numberColumn.iterator();
			Iterator avgIt = avgColumn.iterator();
			Iterator minIt = minColumn.iterator();
			Iterator maxIt = maxColumn.iterator();
			Iterator totalIt = totalColumn.iterator();

			while (nameIt.hasNext()) {
				writer.write('|');
				((ColumnEntry) nameIt.next()).write(writer,
						nameColumn.currentMaxSize);
				writer.write('|');
				((ColumnEntry) numberIt.next()).write(writer,
						numberColumn.currentMaxSize);
				writer.write('|');
				((ColumnEntry) avgIt.next()).write(writer,
						avgColumn.currentMaxSize);
				writer.write('|');
				((ColumnEntry) minIt.next()).write(writer,
						minColumn.currentMaxSize);
				writer.write('|');
				((ColumnEntry) maxIt.next()).write(writer,
						maxColumn.currentMaxSize);
				writer.write('|');
				((ColumnEntry) totalIt.next()).write(writer,
						totalColumn.currentMaxSize);
				writer.write('|');
				writer.write(separator);
			}
		}
	}

	class Column {
		private int currentMaxSize = 0;

		private List entries;

		public Column(String aHeadLine) {
			entries = new ArrayList();
			addEntry(new SeparatorEntry());
			addEntry(new CenteredEntry(aHeadLine));
			addEntry(new SeparatorEntry());
		}

		@SuppressWarnings("unchecked")
		public void addEntry(ColumnEntry entry) {
			int i = entry.getCurrentLength();
			currentMaxSize = currentMaxSize > i ? currentMaxSize : entry
					.getCurrentLength();
			entries.add(entry);
		}

		public Iterator iterator() {
			return entries.iterator();
		}
	}

	interface ColumnEntry {
		public int getCurrentLength();

		public void write(Writer writer, int totalWidth) throws IOException;
	}

	class NestedEntry implements ColumnEntry {
		
		private int nestingLevel;
		private String text;

		public NestedEntry(int aNestingLevel, String aText) {
			nestingLevel = aNestingLevel;
			text = aText;
		}

		public int getCurrentLength() {
			return 2 * nestingLevel + text.length() + 2;
		}

		public void write(Writer writer, int totalWidth) throws IOException {
			writer.write(' ');
			for (int i = 0; i < nestingLevel * 2; i++) {
				writer.write(' ');
			}

			writer.write(text);

			for (int i = 0; i < totalWidth - nestingLevel * 2 - text.length()
					- 2; i++) {
				writer.write(' ');
			}
			writer.write(' ');
		}
	}

	class RightAlignedEntry implements ColumnEntry {
		private String text;

		public RightAlignedEntry(String aText) {
			text = aText;
		}

		public int getCurrentLength() {
			return text.length() + 2;
		}

		public void write(Writer writer, int totalWidth) throws IOException {
			writer.write(' ');
			if (text.length() == totalWidth) {
				writer.write(text);
			} else {
				for (int i = 0; i < totalWidth - text.length() - 2; i++) {
					writer.write(' ');
				}
				writer.write(text);
			}
			writer.write(' ');
		}
	}

	class CenteredEntry implements ColumnEntry {
		private String text;

		public CenteredEntry(String aText) {
			text = aText;
		}

		public int getCurrentLength() {
			return text.length() + 2;
		}

		public void write(Writer writer, int totalWidth) throws IOException {
			if (totalWidth == getCurrentLength()) {
				writer.write(' ');
				writer.write(text);
				writer.write(' ');
			} else {
				int remaining = totalWidth - text.length();
				int prefix;
				int posfix;
				if (remaining % 2 == 1) {
					remaining++;
					prefix = remaining / 2;
					posfix = prefix - 1;
				} else {
					prefix = remaining / 2;
					posfix = prefix;
				}

				for (int i = 0; i < prefix; i++) {
					writer.write(' ');
				}
				writer.write(text);
				for (int i = 0; i < posfix; i++) {
					writer.write(' ');
				}

			}

		}
	}

	class SeparatorEntry implements ColumnEntry {

		public int getCurrentLength() {
			return 0;
		}

		public void write(Writer writer, int totalWidth) throws IOException {
			for (int i = 0; i < totalWidth; i++) {
				writer.write('-');
			}
		}
	}

}
