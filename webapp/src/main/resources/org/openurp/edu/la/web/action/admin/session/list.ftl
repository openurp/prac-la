[#ftl]
[@b.head/]
[@b.grid items=laSessions var="laSession"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="25%" property="name" title="批次名称"/]
    [@b.col width="25%" property="beginAt"  title="开始时间"]${laSession.beginAt! }[/@]
    [@b.col width="25%" property="endAt" title="结束时间"]${laSession.endAt !}[/@]
    [@b.col width="20%" property="grades" title="年级"/]
  [/@]
[/@]
[@b.foot/]
