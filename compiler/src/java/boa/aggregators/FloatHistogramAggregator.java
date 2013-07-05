package boa.aggregators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import boa.io.EmitKey;

/**
 * A Boa aggregator to calculate a histogram for the values in a dataset.
 * 
 * @author anthonyu
 */
@AggregatorSpec(name = "histogram", type = "float", formalParameters = { "int", "int", "int" })
public class FloatHistogramAggregator extends HistogramAggregator {
	private SortedCountingSet<Double> list;

	/**
	 * Construct a FloatHistogramAggregator.
	 * 
	 * @param min
	 *            A long representing the minimum value to be considered in the
	 *            histogram
	 * 
	 * @param max
	 *            A long representing the maximum value to be considered in the
	 *            histogram
	 * 
	 * @param buckets
	 *            A long representing the number of buckets in the histogram
	 */
	public FloatHistogramAggregator(final long min, final long max, final long buckets) {
		super(min, max, buckets);
	}

	/** {@inheritDoc} */
	@Override
	public void start(final EmitKey key) {
		super.start(key);

		this.list = new SortedCountingSet<Double>();
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final String data, final String metadata) throws NumberFormatException, IOException, InterruptedException {
		this.aggregate(Double.valueOf(data), metadata);
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final long data, final String metadata) throws IOException {
		this.aggregate(Long.valueOf(data).doubleValue(), metadata);
	}

	/** {@inheritDoc} */
	@Override
	public void aggregate(final double data, final String metadata) throws IOException {
		this.list.add(Double.valueOf(data), super.count(metadata));
	}

	/** {@inheritDoc} */
	@Override
	public List<Pair<Number, Long>> getTuples() {
		final List<Pair<Number, Long>> list = new ArrayList<Pair<Number, Long>>();

		// convert the map entries into a list of Pair
		for (final Entry<Double, Long> e : this.list.getEntries())
			list.add(new Pair<Number, Long>(e.getKey(), e.getValue()));

		return list;
	}
}
