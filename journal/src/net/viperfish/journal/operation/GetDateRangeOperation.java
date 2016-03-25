package net.viperfish.journal.operation;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.utils.time.TimeUtils;

class GetDateRangeOperation extends OperationWithResult<Set<Journal>> {

	private Date lowerBound;
	private Date upperBound;

	public GetDateRangeOperation(Date lowerBound, Date upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public void execute() {
		List<Journal> all = db().getAll();
		Set<Journal> result = new TreeSet<>();
		for (Journal i : all) {
			Date theDate = TimeUtils.truncDate(i.getDate());
			if (theDate.equals(lowerBound) || theDate.equals(upperBound)) {
				result.add(i);
				continue;
			}
			if (theDate.after(lowerBound) && theDate.before(upperBound)) {
				result.add(i);
			}
		}
		this.setResult(result);
	}

}
