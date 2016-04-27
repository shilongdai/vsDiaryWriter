package net.viperfish.journal.operation;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.viperfish.framework.time.TimeUtils;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalPointer;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * gets all entries within a range of dates
 * 
 * @author sdai
 *
 */
final class GetDateRangeOperation extends OperationWithResult<Set<JournalPointer>> {

	private Date lowerBound;
	private Date upperBound;

	GetDateRangeOperation(Date lowerBound, Date upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public void execute() {
		// get all entries
		List<Journal> all = db().getAll();
		Set<JournalPointer> result = new TreeSet<>();

		// filter
		for (Journal i : all) {
			// truncate timestamp to year, month, and date
			Date theDate = TimeUtils.truncDate(i.getDate());

			// the filter is inclusive
			if (theDate.equals(lowerBound) || theDate.equals(upperBound)) {
				result.add(new JournalPointer(i));
				continue;
			}

			// check if out of range
			if (theDate.after(lowerBound) && theDate.before(upperBound)) {
				result.add(new JournalPointer(i));
			}
		}
		this.setResult(result);
	}

}
