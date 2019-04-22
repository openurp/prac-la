[#ftl]
[@b.head/]
[@b.grid items=laOptions var="laOption"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="11%" property="corporation.name" title="单位名称"][@b.a href="!info?id=${laOption.id}"]${(laOption.corporation.name)!}[/@][/@]
    [@b.col width="35%" property="benefits" title="要求"/]
    [@b.col width="35%" property="request" title="福利条件"/]
    [@b.col width="7%" property="capacity" title="人数上限"/]
    [@b.col width="7%" property="actual" title="实际人数"/]
  [/@]
[/@]
[@b.foot/]
