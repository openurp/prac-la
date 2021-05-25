[#ftl]
[@b.head/]
[@b.grid items=laOptions var="laOption"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
    [#if Parameters['laOption.session.id']?? && Parameters['laOption.session.id']?length>0]
    bar.addItem("自动录取",action.method('autoEnroll',"确定自动录取?"));
    [/#if]
    bar.addItem("面试表",action.multi('report',null,null,"_blank"),"action-print");
  [/@]
  [@b.row]
    [@b.boxcol/]
    [@b.col width="20%" property="corporation.name" title="单位名称"][@b.a href="!info?id=${laOption.id}"]${(laOption.corporation.name)!}[/@][/@]
    [@b.col width="28%" property="requirement" title="要求"/]
    [@b.col width="15%" property="remark" title="备注"/]
    [@b.col width="9%" property="capacity" title="最大面试人数"/]
    [@b.col width="9%" title="实际面试人数"][@b.a href="!volunteers?id=${laOption.id}"]${laOption.volunteers?size}[/@][/@]
    [@b.col width="7%" title="报名人数"][@b.a href="!takers?id=${laOption.id}"]${laOption.takers?size}[/@][/@]
    [@b.col width="8%" property="enrollLimit" title="拟录取人数"/]
  [/@]
[/@]
[@b.foot/]
