##### After XDF-1172 -----------------------------------------------

/dfs/opt/bda/xdf-nextgen-current/bin/execute-component.sh -a xda-ux-sr-comp-dev -b B01 -m transformer -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev/conf/transformer.jconf -r hdfs:///data/bda

-a xda-ux-sr-comp-dev -b B02 -m transformer -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev/conf/transformer_os.jconf -r hdfs:///data/bda 
-a xda-ux-sr-comp-dev -b B03 -m transformer -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev/conf/transformer_jjs.jconf -r hdfs:///data/bda 


/dfs/opt/bda/xdf-ngsr-current/bin/xdf-mdcli.sh file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev-1.0.0_dev/meta/create-ds-transformer.json

/dfs/opt/bda/xdf-nextgen-current/bin/xdf-mdcli.sh file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev-1.0.0_dev/meta/transformer_test_ref1.json
/dfs/opt/bda/xdf-nextgen-current/bin/xdf-mdcli.sh file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev-1.0.0_dev/meta/transformer_test_ref2.json

/dfs/opt/bda/xdf-nextgen-current/bin/execute-component.sh -a xda-ux-sr-comp-dev -b BR01 -m transformer -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev/conf/transformer_ref1.jconf -r hdfs:///data/bda
/dfs/opt/bda/xdf-nextgen-current/bin/execute-component.sh -a xda-ux-sr-comp-dev -b BR02 -m transformer -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev/conf/transformer_ref2.jconf -r hdfs:///data/bda


###### XDF-NG 1196 - 2 -- PM and Transformer TC

/dfs/opt/bda/xdf-ngsr-current/bin/execute-component.sh -a xda-ux-sr-comp-dev -b BTRPM12 -m transformer -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev/conf/transformer_jexl_ss_pm.jconf -r hdfs:///data/bda


val s = "hdfs:///data/bda/xda-ux-sr-comp-dev/dl/fs/dout/TRTEST02_OUTSCH/data/TRTEST02_OUTSCH.BR02.20180208-043423.00000.parquet"

/dfs/opt/bda/xdf-ngsr-current/bin/execute-component.sh -a xda-ux-sr-comp-dev -b BTRPM13 -m transformer -c file:///dfs/opt/bda/apps/xda-ux-sr-comp-dev/conf/transformer_janino_pm.jconf -r hdfs:///data/bda


TRTEST_JEXL_DYNSCHEMA
TRTEST_JEXL_DYNSCHEMA_REJECTED
