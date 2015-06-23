package blade.jetm;

import etm.core.monitor.EtmPoint;

public class JetmPoint {

	private EtmPoint etmPoint;

	public JetmPoint(EtmPoint etmPoint) {
		this.etmPoint = etmPoint;
	}

	public EtmPoint getEtmPoint() {
		return etmPoint;
	}

	public void setEtmPoint(EtmPoint etmPoint) {
		this.etmPoint = etmPoint;
	}

	public void collect() {
		if (null != this.etmPoint) {
			this.etmPoint.collect();
		}
	}
}