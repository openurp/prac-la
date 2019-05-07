[#ftl]
[@b.head/]
[@b.grid items=laOptions var="option"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="20%" property="corporation.name" title="单位名称"][@b.a href="!info?id=${option.id}"]${(option.corporation.name)!}[/@][/@]
    [@b.col width="35%" property="requirement" title="要求"/]
    [@b.col width="20%" property="remark" title="备注"/]
    [@b.col width="7%" property="capacity" title="人数上限"/]
    [@b.col width="7%" property="enrollLimit" title="拟录取"/]
    [@b.col width="7%" property="actual" title="报名人数"][@b.a href="!volunteers?id=${option.id}"]${option.actual}[/@][/@]
  [/@]
[/@]
[@b.foot/]
