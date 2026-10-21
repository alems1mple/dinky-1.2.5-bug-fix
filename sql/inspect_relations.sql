-- Read-only diagnostics for DolphinScheduler 3.2.2.
-- Replace 123456789012345 with a workflow definition code.
SELECT process_definition_version AS workflow_version,
       COUNT(*) AS total_relations,
       COUNT(DISTINCT pre_task_code, post_task_code) AS unique_edges,
       COUNT(*) - COUNT(DISTINCT pre_task_code, post_task_code) AS duplicate_edges
FROM t_ds_process_task_relation_log
WHERE process_definition_code = 123456789012345
GROUP BY process_definition_version
ORDER BY process_definition_version;

-- Current active workflow relations.
SELECT p.version AS workflow_version,
       COUNT(r.id) AS total_relations,
       COUNT(DISTINCT r.pre_task_code, r.post_task_code) AS unique_edges,
       COUNT(r.id) - COUNT(DISTINCT r.pre_task_code, r.post_task_code) AS duplicate_edges
FROM t_ds_process_definition p
LEFT JOIN t_ds_process_task_relation r
  ON p.code = r.process_definition_code
 AND p.version = r.process_definition_version
WHERE p.code = 123456789012345
GROUP BY p.version;
