Process States
--------------
**Three States**
> Ready, Running, Blocked

Only 1 Process Running at all times

Mandatory Commands
---------------------
init
cr <name> <priority>
de <name>
req <resource> <# of unit>
rel <resource> <# of unit>
to

Possible Transitions / Operations
---------------------
Create     | None > Ready
Destroy    | Any > None
Request    | Running > Blocked (If Resource Not Available)
Scheduler  | Ready > Running | Running > Ready
Release    | Blocked > Ready (Release from resource)
Time_out   | Running > Ready (Take Out from CPU)

PCB
---
- PID : String (Name of Process)
- Other_resources : LinkedList<Resources> (Resources Acquired during Runtime)
- Status_type : Ready / Blocked
- Status_list : ReadyList / Block List (Back Pointer)
- Creation_tree Parent : Process
- Creation_tree Child : LinkedList<Process>
- Priority : Enum [0,1,2 (Init, User, System)]

Ready List (RL)
---------------
Enum Representation
0 INIT
1 USER
2 SYSTEM

- 3 LinkedList for each Priority
- INIT_PROCESS run on startup as initial process
- Priorities don't change
- Process either RL or BL

Create Method
--------------
Create(String name, TYPE priority){
  create PCB data structure
  initialise PCB using Parameters
  Link PCB to creation tree
  insert(RL, PCB)
  Scheduler()
}

- Init process is crated at start-up & can create first
system or user process
- Any new or released process is inserted at the end of queue
(RL) (Design Decision)

Destroy Method
--------------
Destroy (pid) {
  get pointer p to PCB using pid
  Kill_Tree(p)
  Scheduler
}

Kill_Tree (p) { // recursive call
  for all child process q kill_tree(q)
  free resources
  delete PCB and update all the pointers
}

- Process can be destroyed


Resource (RCB)
--------------
- Fixed Set of Resources
- RID : String
- Status k : initial free units
- Status u : current free units
- Waiting_list : LinkedList<Process> (Block Process with no avail free units)

Request Resource (1-Unit) -> Need to Extend Multiple Units
----------------
Request (rid, int n) {
  r = Get_RCB(rid);
  if(n <= this.u){
    u -= n;
    insert(other_Resources, n);
  } else {
    Status.Type = 'blocked';
    Status.list = r;
    remove (RL, self);
    insert (r->waiting_list, self);
    Scheduler();
  }
}

- Strict FIFO. Must strictly go in queue order.

Release Resource
----------------
Release (rid) {
  r = Get_RCB(rid);
  remove(self -> other_resources, r);
  r.u += n;

  while(r->waiting_list != empty && u >= req){
    // remove one by one from waiting list.
  }

  scheduler();
}

Scheduling
----------
- 3 Level Priority Scheduler
- Use Preemptive RR Scheduling with Level
- Time Sharing is Simulated by Function Call
- 2 Way move to next process. Either Blocked or TO

Scheduler
---------
- Function at the end of every kernel (method) call
Scheduler(){
  find highest priority process p
  if(current.priority < p.priority || self.status.type != 'running'
  || self == NIL)
  preempt(p, self) //swap
}

Condition (1) : Called from Create or Release
Condition (2) : Called from Request or Time-Out
Condition (3) : Called from Destroy

Preempt
- Change status of p to running
- Context Switch (Output name of running process)

Time-out interupts
------------------
- Simulate Time-Sharing

Time_out() {
  find running process q;
  remove(RL, q);
  q.statustype = 'ready'
  insert(RL, q); // simulate RR Timesharing
  Scheduler();
}


Mandatory Commands
---------------------
init
cr <name> <priority>
de <name>
req <resource> <# of unit>
rel <resource> <# of unit>
to

init - no validation (will always work)
other commands - error

cr
--
must have 2 argument else error (DONE)
last argument MUST be integer (DONE)
process name must be unique (ignore case) (DONE)
priority only 1 or 2 else error (DONE)
must not be init (DONE)

de
--
must have 1 string argument ONLY (DONE)
the argument must be a existing process in the system (DONE)
cannot be init (DONE)

req
--
MUST HAVE 2 ARGUMENT (STRING) (INTEGER) (DONE)
CANNOT RUN COMMAND FROM INIT (DONE)
resource must be existing resource in system  (DONE)
if number of unit < 0 or > maxCapacity => error. (DONE)
if 0 then just print. (DONE)

rel
--
MUST HAVE 2 ARGUMENT (STRING) (INTEGER) (DONE)
CANNOT RUN COMMAND FROM INIT (DONE)
resource must be existing resource in system (DONE)
resource must be existing resource in running process (DONE)
number of unit release < 0 or > held units by running process-> error  (DONE)
if 0 then just nothin happen, just print. (DONE)

to
--
nothing

