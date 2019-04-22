[#ftl]
[@b.head/]
[@b.grid items=corporations var="corporation"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="15%" property="name" title="单位名称"/]
    [@b.col width="40%" property="request" title="要求"/]
    [@b.col width="40%" property="benefits" title="福利条件"/]
  [/@]
[/@]
[@b.foot/]
