[#ftl]
[@b.head/]
[@b.grid items=laDeparts var="laDepart"]
  [@b.gridbar]
    bar.addItem("${b.text("action.new")}",action.add());
    bar.addItem("${b.text("action.modify")}",action.edit());
    bar.addItem("${b.text("action.delete")}",action.remove("确认删除?"));
  [/@]
  [@b.row]
    [@b.boxcol /]
    [@b.col width="30%" property="name" title="单位名称"][@b.a href="!info?id=${laDepart.id}"]${laDepart.name}[/@][/@]
    [@b.col width="30%" property="opened" title="是否开放"]${laDepart.opened?string('是','否') }[/@]
    [@b.col width="30%" property="capacity" title="人数上限"/]
  [/@]
[/@]
[@b.foot/]
