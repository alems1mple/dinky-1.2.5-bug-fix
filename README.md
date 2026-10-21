# Dinky / DolphinScheduler DAG Relation Deduplication Fix

Unofficial community hotfix for Dinky 1.2.5 and DolphinScheduler 3.2.2. Not an official Dinky release.

## Problem

Repeated Dinky task publication caused the number of relation records in an existing DolphinScheduler workflow to grow rapidly, making the DAG view slow or unresponsive.

| Version | Total relations | Unique edges | Duplicates |
| --- | ---: | ---: | ---: |
| 25 | 10,239 | 19 | 10,220 |
| 26 | 20,479 | 20 | 20,459 |
| 27 | 40,959 | 21 | 40,938 |
| 28 (patched) | 21 | 21 | 0 |

## Fix

The source patch adds defensive deduplication to `SchedulerServiceImpl.updateProcessDefinition()`. It retains one relation per `(preTaskCode, postTaskCode)` pair in insertion order and rejects conflicts in task versions or condition metadata.

- [Java source patch](patches/dinky-1.2.5-dag-relation-dedup.patch)
- [Standalone Java 8 example](examples/RelationDeduplicationDemo.java)
- [Diagnostic SQL](sql/inspect_relations.sql)

## Production validation

Dinky logs showed `before=40959, after=21` and `before=77, after=13`. After subsequent publications, the current ODS, DIM and DWD workflows each had zero duplicate DAG relations. This validates the defensive guard, but does **not** establish where duplicated relations first originate.

## Apply

Clone the [Dinky 1.2 source](https://github.com/DataLinkDC/dinky/tree/1.2), copy the patch into the checkout, and run:

```bash
git apply --check dinky-1.2.5-dag-relation-dedup.patch
git apply dinky-1.2.5-dag-relation-dedup.patch
```

Build and test against the matching Dinky version before deployment. The patch is a reconstruction of the final production-tested logic, not a byte-for-byte export of the deployed JAR. Full upstream-build testing and clean-install reproduction have not been independently performed.

## Upstream

[DataLinkDC/dinky](https://github.com/DataLinkDC/dinky) - community research and defensive patch for review.
