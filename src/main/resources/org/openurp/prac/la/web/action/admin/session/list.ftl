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
    [@b.col property="name" title="批次名称"/]
    [@b.col width="25%" property="beginAt"  title="开始时间"]${laSession.beginAt!}~${laSession.endAt!}[/@]
    [@b.col width="15%" property="grades" title="年级"/]
    [@b.col width="10%" property="minGpa" title="最低平均绩点"/]
    [@b.col width="10%" property="optionCount" title="志愿数目"/]
    [@b.col width="10%" property="attachmentRequired" title="需要简历"]
      ${laSession.attachmentRequired?string('需要简历','不要求')}
    [/@]
  [/@]
[/@]
[@b.foot/]
