package net.viperfish.journal.operation;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.utils.time.TimeUtils;

/**
 * gets all entries within a range of dates
 * 
 * @author sdai
 *
 */
class GetDateRangeOperation extends OperationWithResult<Set<Journal>> {

	private Date lowerBound;
	private Date upperBound;

	public GetDateRangeOperation(Date lowerBound, Date upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public void execute() {
		// get all entries
		List<Journal> all = db().getAll();
		Set<Journal> result = new TreeSet<>();

		// filter
		for (Journal i : all) {
			// truncate timestamp to year, month, and date
			Date theDate = TimeUtils.truncDate(i.getDate());

			// the filter is inclusive
			if (theDate.equals(lowerBound) || theDate.equals(upperBound)) {
				result.add(i);
				continue;
			}

			// check if out of range
			if (theDate.after(lowerBound) && theDate.before(upperBound)) {
				result.add(i);
			}
		}
		this.setResult(result);
	}

}
