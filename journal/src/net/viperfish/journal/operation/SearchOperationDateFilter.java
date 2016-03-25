package net.viperfish.journal.operation;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.utils.time.TimeUtils;

/**
 * filters the result from a search operation with date range
 * 
 * @author sdai
 *
 */
class SearchOperationDateFilter extends OperationWithResult<Set<Journal>> {

	private SearchEntryOperation ops;
	private Date upperBound;
	private Date lowerBound;

	private Set<Journal> filterByDate(Collection<Journal> results) {
		Set<Journal> filtered = new HashSet<>();
		for (Journal i : results) {
			// truncate date to only year month date
			Date entryDate = TimeUtils.truncDate(i.getDate());
			// inclusive filter
			if (entryDate.equals(lowerBound) || entryDate.equals(upperBound)) {
				filtered.add(i);
				continue;
			}
			// filter range
			if (i.getDate().before(upperBound) && i.getDate().after(lowerBound)) {
				filtered.add(i);
			}
		}
		return filtered;
	}

	public SearchOperationDateFilter(SearchEntryOperation wrap, Date upperBound, Date lowerBound) {
		this.ops = wrap;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	@Override
	public void execute() {
		ops.execute();
		Set<Journal> tmp = ops.getResult();
		Set<Journal> result = filterByDate(tmp);
		setResult(result);
	}

}
